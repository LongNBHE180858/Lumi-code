package com.lumi.service;

import com.lumi.domain.*;
import com.lumi.domain.enumeration.ClassMemberStatus;
import com.lumi.domain.enumeration.RecordStatus;
import com.lumi.repository.*;
import com.lumi.security.SecurityUtils;
import com.lumi.service.dto.studyClass.ClassExamDTO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class ClassroomExamService {

    private final StudyClassRepository classes;
    private final ClassMemberRepository members;
    private final ClassMaterialRepository materials;
    private final UserRepository users;
    private final ExamRepository exams;
    private final ExamQuestionRepository examQuestions;
    private final QuestionRepository questions;
    private final QuestionOptionRepository options;
    private final ExamAttemptRepository attempts;
    private final ExamAnswerRepository answers;
    private final QuestionBankRepository questionBanks;
    private final QuestionKnowledgeLinkRepository questionKnowledgeLinks;

    public ClassroomExamService(
        StudyClassRepository classes,
        ClassMemberRepository members,
        ClassMaterialRepository materials,
        UserRepository users,
        ExamRepository exams,
        ExamQuestionRepository examQuestions,
        QuestionRepository questions,
        QuestionOptionRepository options,
        ExamAttemptRepository attempts,
        ExamAnswerRepository answers,
        QuestionBankRepository questionBanks,
        QuestionKnowledgeLinkRepository questionKnowledgeLinks
    ) {
        this.classes = classes;
        this.members = members;
        this.materials = materials;
        this.users = users;
        this.exams = exams;
        this.examQuestions = examQuestions;
        this.questions = questions;
        this.options = options;
        this.attempts = attempts;
        this.answers = answers;
        this.questionBanks = questionBanks;
        this.questionKnowledgeLinks = questionKnowledgeLinks;
    }

    private User currentUser() {
        return users
            .findOneByLogin(SecurityUtils.getCurrentUserLogin().orElse(""))
            .filter(User::isActivated)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private StudyClass ownedClass(Long classId, User instructor) {
        StudyClass studyClass = classes
            .findById(classId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp"));
        if (Boolean.TRUE.equals(studyClass.getIsDeleted()) || !Objects.equals(studyClass.getInstructor().getId(), instructor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không quản lý lớp này");
        }
        return studyClass;
    }

    private StudyClass joinedClass(Long classId, User student) {
        StudyClass studyClass = classes
            .findById(classId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp"));
        if (
            Boolean.TRUE.equals(studyClass.getIsDeleted()) ||
            members
                .findByStudyClassIdAndStudentId(classId, student.getId())
                .filter(member -> ClassMemberStatus.ACTIVE.name().equals(member.getStatus()))
                .isEmpty()
        ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn chưa được vào lớp");
        }
        return studyClass;
    }

    private Exam classExam(Long classId, Long examId) {
        Exam exam = exams
            .findById(examId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài kiểm tra"));
        if (
            Boolean.TRUE.equals(exam.getIsDeleted()) ||
            exam.getStudyClass() == null ||
            !Objects.equals(exam.getStudyClass().getId(), classId)
        ) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài kiểm tra");
        }
        return exam;
    }

    private ClassExamDTO.Summary summary(Exam exam) {
        return new ClassExamDTO.Summary(
            exam.getId(),
            exam.getClassMaterial() == null ? null : exam.getClassMaterial().getId(),
            exam.getTitle(),
            exam.getDurationMinutes(),
            exam.getPassScore(),
            exam.getStatus().name(),
            (int) examQuestions.countByExamId(exam.getId()),
            exam.getAssignedStudent() == null ? null : exam.getAssignedStudent().getId()
        );
    }

    @Transactional(readOnly = true)
    public List<ClassExamDTO.Summary> instructorExams(Long classId) {
        ownedClass(classId, currentUser());
        return exams.findByStudyClassIdAndIsDeletedFalseOrderByCreatedAtDesc(classId).stream().map(this::summary).toList();
    }

    @Transactional(readOnly = true)
    public ClassExamDTO.Editor instructorExam(Long classId, Long examId) {
        ownedClass(classId, currentUser());
        Exam exam = classExam(classId, examId);
        List<ClassExamDTO.QuestionInput> items = examQuestions
            .findByExamIdOrderByOrderIndexAsc(examId)
            .stream()
            .map(item -> {
                List<QuestionOption> choices = options.findByQuestionIdOrderByOrderIndexAsc(item.getQuestion().getId());
                int correct = -1;
                for (int index = 0; index < choices.size(); index++) {
                    if (Boolean.TRUE.equals(choices.get(index).getIsCorrect())) correct = index;
                }
                return new ClassExamDTO.QuestionInput(
                    item.getQuestion().getContent(),
                    item.getQuestion().getExplanation(),
                    item.getScore(),
                    choices
                        .stream()
                        .map(choice -> new ClassExamDTO.OptionInput(choice.getContent()))
                        .toList(),
                    correct
                );
            })
            .toList();
        return new ClassExamDTO.Editor(summary(exam), items);
    }

    @Transactional(readOnly = true)
    public List<ClassExamDTO.Summary> studentExams(Long classId) {
        User student = currentUser();
        joinedClass(classId, student);
        Instant now = Instant.now();
        return exams
            .findByStudyClassIdAndIsDeletedFalseOrderByCreatedAtDesc(classId)
            .stream()
            .filter(exam -> {
                boolean submitted = attempts
                    .findByExamIdAndStudentIdOrderByStartTimeDesc(exam.getId(), student.getId())
                    .stream()
                    .anyMatch(attempt -> "SUBMITTED".equals(attempt.getStatus()));
                boolean available =
                    exam.getStatus() == RecordStatus.ACTIVE &&
                    (exam.getAssignedStudent() == null || Objects.equals(exam.getAssignedStudent().getId(), student.getId())) &&
                    (exam.getOpensAt() == null || !exam.getOpensAt().isAfter(now)) &&
                    (exam.getClosesAt() == null || exam.getClosesAt().isAfter(now)) &&
                    exam.getClassMaterial() != null &&
                    "PUBLISHED".equals(exam.getClassMaterial().getStatus());
                return submitted || available;
            })
            .map(this::summary)
            .toList();
    }

    public ClassExamDTO.Summary createExam(Long classId, ClassExamDTO.SaveExam input) {
        User instructor = currentUser();
        StudyClass studyClass = ownedClass(classId, instructor);
        ClassMaterial material = materials
            .findById(input.materialId())
            .filter(item -> Objects.equals(item.getStudyClass().getId(), classId))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bài học không thuộc lớp này"));
        User assignedStudent = null;
        if (input.assignedStudentId() != null) {
            if (
                members
                    .findByStudyClassIdAndStudentId(classId, input.assignedStudentId())
                    .filter(member -> ClassMemberStatus.ACTIVE.name().equals(member.getStatus()))
                    .isEmpty()
            ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Học sinh chưa tham gia lớp");
            }
            assignedStudent = users.findById(input.assignedStudentId()).orElseThrow();
        }
        if (
            input
                .questions()
                .stream()
                .anyMatch(question -> question.correctIndex() >= question.options().size())
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đáp án đúng vượt quá số lựa chọn");
        }
        Exam exam = exams.saveAndFlush(
            new Exam()
                .studyClass(studyClass)
                .classMaterial(material)
                .createdBy(instructor)
                .assignedStudent(assignedStudent)
                .title(input.title().trim())
                .durationMinutes(input.durationMinutes())
                .passScore(input.passScore())
                .maxAttempts(3)
                .shuffleQuestions(false)
                .status(RecordStatus.ACTIVE)
                .isDeleted(false)
                .resultReleaseMode("IMMEDIATE")
                .answerReleaseMode("AFTER_SUBMIT")
                .version(1L)
                .createdAt(Instant.now())
                .assessmentType("EXAM")
                .assessmentConfig("{}")
                .securityConfig("{}")
        );
        saveQuestions(exam, instructor, input.questions());
        return summary(exam);
    }

    public List<ClassExamDTO.PickedQuestion> generatePreview(Long classId, ClassExamDTO.GeneratePreviewRequest input) {
        User instructor = currentUser();
        ownedClass(classId, instructor);
        QuestionBank bank = questionBanks
            .findById(input.bankId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy ngân hàng câu hỏi"));
        if (!Objects.equals(bank.getInstructor().getId(), instructor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền dùng ngân hàng này");
        }

        List<Question> allQuestions = questions.findByBankIdAndIsDeletedFalseOrderByCreatedAtDesc(input.bankId());
        List<QuestionKnowledgeLink> links = questionKnowledgeLinks.findByBankId(input.bankId());
        Map<Long, String> questionTopicMap = new HashMap<>();
        for (QuestionKnowledgeLink link : links) {
            if (link.getKnowledgeNode() != null) {
                questionTopicMap.put(link.getQuestion().getId(), link.getKnowledgeNode().getTitle());
            }
        }

        Set<Long> selectedIds = new HashSet<>();
        List<ClassExamDTO.PickedQuestion> picked = new ArrayList<>();

        for (ClassExamDTO.MatrixRow row : input.matrix()) {
            if (row.count() == null || row.count() <= 0) continue;
            List<Question> matching = new ArrayList<>();
            for (Question q : allQuestions) {
                if (selectedIds.contains(q.getId())) continue;
                String qTopic = questionTopicMap.getOrDefault(q.getId(), "Mặc định");
                if (
                    row.topic() != null &&
                    !row.topic().isBlank() &&
                    !row.topic().equalsIgnoreCase("ALL") &&
                    !qTopic.equalsIgnoreCase(row.topic().trim())
                ) {
                    continue;
                }
                if (row.difficulty() != null && !Objects.equals(q.getDifficulty(), row.difficulty())) {
                    continue;
                }
                matching.add(q);
            }

            if (matching.size() < row.count()) {
                String topicLabel =
                    row.topic() == null || row.topic().isBlank() || row.topic().equalsIgnoreCase("ALL") ? "Tất cả chủ đề" : row.topic();
                String diffLabel = row.difficulty() == null ? "Bất kỳ" : "Độ khó " + row.difficulty();
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ngân hàng không đủ câu hỏi cho yêu cầu: [" +
                        topicLabel +
                        " - " +
                        diffLabel +
                        "]. Khả dụng: " +
                        matching.size() +
                        " câu, yêu cầu: " +
                        row.count() +
                        " câu."
                );
            }

            Collections.shuffle(matching);
            List<Question> chosen = matching.subList(0, row.count());
            for (Question q : chosen) {
                selectedIds.add(q.getId());
                List<QuestionOption> opts = options.findByQuestionIdOrderByOrderIndexAsc(q.getId());
                int correctIdx = -1;
                List<String> optionContents = new ArrayList<>();
                for (int i = 0; i < opts.size(); i++) {
                    optionContents.add(opts.get(i).getContent());
                    if (Boolean.TRUE.equals(opts.get(i).getIsCorrect())) {
                        correctIdx = i;
                    }
                }
                BigDecimal pts = row.points() != null && row.points().compareTo(BigDecimal.ZERO) > 0 ? row.points() : BigDecimal.ONE;
                picked.add(
                    new ClassExamDTO.PickedQuestion(
                        q.getId(),
                        q.getContent(),
                        q.getDifficulty(),
                        questionTopicMap.getOrDefault(q.getId(), "Mặc định"),
                        pts,
                        optionContents,
                        correctIdx,
                        q.getExplanation()
                    )
                );
            }
        }

        return picked;
    }

    public ClassExamDTO.Summary createExamFromMatrix(Long classId, ClassExamDTO.CreateFromMatrixRequest input) {
        User instructor = currentUser();
        StudyClass studyClass = ownedClass(classId, instructor);
        QuestionBank bank = questionBanks
            .findById(input.bankId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy ngân hàng câu hỏi"));
        if (!Objects.equals(bank.getInstructor().getId(), instructor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền dùng ngân hàng này");
        }

        ClassMaterial material = null;
        if (input.materialId() != null) {
            material = materials
                .findById(input.materialId())
                .filter(item -> Objects.equals(item.getStudyClass().getId(), classId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bài học không thuộc lớp này"));
        }

        User assignedStudent = null;
        if (input.assignedStudentId() != null) {
            if (
                members
                    .findByStudyClassIdAndStudentId(classId, input.assignedStudentId())
                    .filter(member -> ClassMemberStatus.ACTIVE.name().equals(member.getStatus()))
                    .isEmpty()
            ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Học sinh chưa tham gia lớp");
            }
            assignedStudent = users.findById(input.assignedStudentId()).orElseThrow();
        }

        if (input.questionIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Danh sách câu hỏi không được rỗng");
        }

        Exam exam = exams.saveAndFlush(
            new Exam()
                .studyClass(studyClass)
                .classMaterial(material)
                .createdBy(instructor)
                .assignedStudent(assignedStudent)
                .title(input.title().trim())
                .durationMinutes(input.durationMinutes())
                .passScore(input.passScore())
                .maxAttempts(3)
                .shuffleQuestions(false)
                .status(RecordStatus.ACTIVE)
                .isDeleted(false)
                .resultReleaseMode("IMMEDIATE")
                .answerReleaseMode("AFTER_SUBMIT")
                .version(1L)
                .createdAt(Instant.now())
                .assessmentType("EXAM")
                .assessmentConfig("{}")
                .securityConfig("{}")
        );

        BigDecimal pts =
            input.pointsPerQuestion() != null && input.pointsPerQuestion().compareTo(BigDecimal.ZERO) > 0
                ? input.pointsPerQuestion()
                : BigDecimal.ONE;

        for (int i = 0; i < input.questionIds().size(); i++) {
            Long qId = input.questionIds().get(i);
            Question q = questions
                .findById(qId)
                .filter(item -> !Boolean.TRUE.equals(item.getIsDeleted()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Câu hỏi id=" + qId + " không tồn tại"));
            examQuestions.save(new ExamQuestion().exam(exam).question(q).orderIndex(i).score(pts));
        }

        return summary(exam);
    }

    private void saveQuestions(Exam exam, User instructor, List<ClassExamDTO.QuestionInput> inputQuestions) {
        for (int index = 0; index < inputQuestions.size(); index++) {
            ClassExamDTO.QuestionInput questionInput = inputQuestions.get(index);
            Question question = questions.saveAndFlush(
                new Question()
                    .content(questionInput.content().trim())
                    .type("SINGLE_CHOICE")
                    .explanation(questionInput.explanation())
                    .isDeleted(false)
                    .sourceType("INSTRUCTOR")
                    .visibility("PRIVATE")
                    .version(1L)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .createdBy(instructor)
            );
            examQuestions.save(new ExamQuestion().exam(exam).question(question).orderIndex(index).score(questionInput.points()));
            for (int optionIndex = 0; optionIndex < questionInput.options().size(); optionIndex++) {
                options.save(
                    new QuestionOption()
                        .question(question)
                        .orderIndex(optionIndex)
                        .content(questionInput.options().get(optionIndex).content().trim())
                        .isCorrect(optionIndex == questionInput.correctIndex())
                );
            }
        }
    }

    public ClassExamDTO.Summary updateExam(Long classId, Long examId, ClassExamDTO.UpdateExam input) {
        User instructor = currentUser();
        ownedClass(classId, instructor);
        Exam exam = classExam(classId, examId);
        if (!Set.of("ACTIVE", "ARCHIVED").contains(input.status())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trạng thái bài kiểm tra không hợp lệ");
        }
        if (
            input.assignedStudentId() != null &&
            members
                .findByStudyClassIdAndStudentId(classId, input.assignedStudentId())
                .filter(member -> ClassMemberStatus.ACTIVE.name().equals(member.getStatus()))
                .isEmpty()
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Học sinh chưa tham gia lớp");
        }
        if (
            attempts.existsByExamId(examId) &&
            (input.questions() != null ||
                exam.getDurationMinutes().intValue() != input.durationMinutes() ||
                exam.getPassScore().compareTo(input.passScore()) != 0 ||
                !Objects.equals(exam.getAssignedStudent() == null ? null : exam.getAssignedStudent().getId(), input.assignedStudentId()))
        ) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Không thể đổi thời gian, điểm đạt hoặc người nhận sau khi học sinh bắt đầu làm bài"
            );
        }
        if (input.questions() != null) {
            if (
                input.questions().isEmpty() ||
                input.questions().size() > 30 ||
                input
                    .questions()
                    .stream()
                    .anyMatch(
                        question ->
                            question.correctIndex() == null ||
                            question.correctIndex() < 0 ||
                            question.correctIndex() >= question.options().size()
                    )
            ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Danh sách câu hỏi không hợp lệ");
            }
            examQuestions.deleteAll(examQuestions.findByExamIdOrderByOrderIndexAsc(examId));
            examQuestions.flush();
            saveQuestions(exam, instructor, input.questions());
        }
        exam.setTitle(input.title().trim());
        exam.setDurationMinutes(input.durationMinutes());
        exam.setPassScore(input.passScore());
        exam.setAssignedStudent(input.assignedStudentId() == null ? null : users.findById(input.assignedStudentId()).orElseThrow());
        exam.setStatus(RecordStatus.valueOf(input.status()));
        exam.setVersion(exam.getVersion() + 1);
        return summary(exams.save(exam));
    }

    public ClassExamDTO.AttemptView startAttempt(Long classId, Long examId) {
        User student = currentUser();
        joinedClass(classId, student);
        Exam exam = exams
            .findByIdForUpdate(examId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài kiểm tra"));
        if (
            Boolean.TRUE.equals(exam.getIsDeleted()) ||
            exam.getStudyClass() == null ||
            !Objects.equals(exam.getStudyClass().getId(), classId)
        ) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài kiểm tra");
        }
        Instant now = Instant.now();
        if (
            exam.getStatus() != RecordStatus.ACTIVE ||
            (exam.getAssignedStudent() != null && !Objects.equals(exam.getAssignedStudent().getId(), student.getId())) ||
            (exam.getOpensAt() != null && exam.getOpensAt().isAfter(now)) ||
            (exam.getClosesAt() != null && !exam.getClosesAt().isAfter(now)) ||
            exam.getClassMaterial() == null ||
            !"PUBLISHED".equals(exam.getClassMaterial().getStatus())
        ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bài kiểm tra chưa mở cho bạn");
        }
        List<ExamAttempt> previous = attempts.findByExamIdAndStudentIdOrderByStartTimeDesc(examId, student.getId());
        ExamAttempt attempt = previous
            .stream()
            .filter(item -> "IN_PROGRESS".equals(item.getStatus()) && item.getDeadlineAt().isAfter(now))
            .findFirst()
            .orElse(null);
        if (attempt == null) {
            if (exam.getMaxAttempts() != null && previous.size() >= exam.getMaxAttempts()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Bạn đã dùng hết số lần làm bài");
            }
            attempt = attempts.save(
                new ExamAttempt()
                    .exam(exam)
                    .student(student)
                    .startTime(now)
                    .deadlineAt(now.plusSeconds(exam.getDurationMinutes() * 60L))
                    .status("IN_PROGRESS")
                    .rowVersion(0L)
                    .monitoringStatus("NONE")
            );
        }
        List<ClassExamDTO.QuestionView> views = examQuestions
            .findByExamIdOrderByOrderIndexAsc(examId)
            .stream()
            .map(item ->
                new ClassExamDTO.QuestionView(
                    item.getQuestion().getId(),
                    item.getQuestion().getContent(),
                    item.getScore(),
                    options
                        .findByQuestionIdOrderByOrderIndexAsc(item.getQuestion().getId())
                        .stream()
                        .map(option -> new ClassExamDTO.Option(option.getId(), option.getContent()))
                        .toList()
                )
            )
            .toList();
        return new ClassExamDTO.AttemptView(attempt.getId(), examId, attempt.getDeadlineAt(), views);
    }

    public ClassExamDTO.Result submit(Long classId, Long examId, Long attemptId, ClassExamDTO.Submit input) {
        User student = currentUser();
        joinedClass(classId, student);
        Exam exam = classExam(classId, examId);
        ExamAttempt attempt = attempts
            .findByIdForUpdate(attemptId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lượt làm bài"));
        Instant now = Instant.now();
        if (
            !Objects.equals(attempt.getExam().getId(), examId) ||
            !Objects.equals(attempt.getStudent().getId(), student.getId()) ||
            !"IN_PROGRESS".equals(attempt.getStatus()) ||
            !attempt.getDeadlineAt().isAfter(now)
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lượt làm bài đã hết hạn hoặc đã nộp");
        }
        List<ExamQuestion> examItems = examQuestions.findByExamIdOrderByOrderIndexAsc(examId);
        Map<Long, Long> submitted = new HashMap<>();
        for (ClassExamDTO.AnswerInput answer : input.answers()) {
            if (submitted.putIfAbsent(answer.questionId(), answer.optionId() == null ? -1L : answer.optionId()) != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Câu hỏi được trả lời nhiều lần");
            }
        }
        if (
            submitted
                .keySet()
                .stream()
                .anyMatch(id -> examItems.stream().noneMatch(item -> Objects.equals(item.getQuestion().getId(), id)))
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đáp án không thuộc bài kiểm tra");
        }
        BigDecimal earned = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        for (ExamQuestion item : examItems) {
            Long questionId = item.getQuestion().getId();
            List<QuestionOption> choices = options.findByQuestionIdOrderByOrderIndexAsc(questionId);
            Long selectedId = submitted.get(questionId);
            QuestionOption selected =
                selectedId == null || selectedId == -1L
                    ? null
                    : choices
                          .stream()
                          .filter(choice -> Objects.equals(choice.getId(), selectedId))
                          .findFirst()
                          .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lựa chọn không thuộc câu hỏi"));
            boolean correct = selected != null && Boolean.TRUE.equals(selected.getIsCorrect());
            total = total.add(item.getScore());
            if (correct) earned = earned.add(item.getScore());
            answers.save(
                new ExamAnswer()
                    .attempt(attempt)
                    .question(item.getQuestion())
                    .option(selected)
                    .isCorrect(correct)
                    .score(correct ? item.getScore() : BigDecimal.ZERO)
                    .updatedAt(now)
                    .rowVersion(0L)
            );
        }
        attempt.setScore(
            total.signum() == 0 ? BigDecimal.ZERO : earned.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP)
        );
        attempt.setStatus("SUBMITTED");
        attempt.setSubmittedAt(now);
        attempt.setEndTime(now);
        attempts.save(attempt);
        return result(attempt, exam);
    }

    @Transactional(readOnly = true)
    public ClassExamDTO.Result studentResult(Long classId, Long examId, Long attemptId) {
        User student = currentUser();
        joinedClass(classId, student);
        Exam exam = classExam(classId, examId);
        ExamAttempt attempt = attempts.findById(attemptId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (
            !Objects.equals(attempt.getExam().getId(), examId) ||
            !Objects.equals(attempt.getStudent().getId(), student.getId()) ||
            !"SUBMITTED".equals(attempt.getStatus())
        ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không được xem kết quả này");
        }
        return result(attempt, exam);
    }

    @Transactional(readOnly = true)
    public List<ClassExamDTO.Result> studentResults(Long classId, Long examId) {
        User student = currentUser();
        joinedClass(classId, student);
        Exam exam = classExam(classId, examId);
        return attempts
            .findByExamIdAndStudentIdOrderByStartTimeDesc(examId, student.getId())
            .stream()
            .filter(attempt -> "SUBMITTED".equals(attempt.getStatus()))
            .map(attempt -> result(attempt, exam))
            .toList();
    }

    private ClassExamDTO.Result result(ExamAttempt attempt, Exam exam) {
        List<ClassExamDTO.Feedback> feedback = answers
            .findByAttemptId(attempt.getId())
            .stream()
            .map(answer -> {
                List<QuestionOption> choices = options.findByQuestionIdOrderByOrderIndexAsc(answer.getQuestion().getId());
                String correct = choices
                    .stream()
                    .filter(option -> Boolean.TRUE.equals(option.getIsCorrect()))
                    .map(QuestionOption::getContent)
                    .findFirst()
                    .orElse("");
                return new ClassExamDTO.Feedback(
                    answer.getQuestion().getId(),
                    answer.getQuestion().getContent(),
                    answer.getOption() == null ? null : answer.getOption().getContent(),
                    correct,
                    Boolean.TRUE.equals(answer.getIsCorrect()),
                    answer.getQuestion().getExplanation()
                );
            })
            .toList();
        return new ClassExamDTO.Result(
            attempt.getId(),
            attempt.getScore(),
            attempt.getScore().compareTo(exam.getPassScore()) >= 0,
            attempt.getSubmittedAt(),
            feedback
        );
    }

    @Transactional(readOnly = true)
    public ClassExamDTO.Progress progress(Long classId) {
        ownedClass(classId, currentUser());
        List<Exam> classExams = exams.findByStudyClassIdAndIsDeletedFalseOrderByCreatedAtDesc(classId);
        List<ExamAttempt> submitted = attempts.findByExamStudyClassIdAndStatus(classId, "SUBMITTED");
        List<ClassExamDTO.StudentProgress> students = members
            .findByStudyClassIdOrderByRequestedAtAsc(classId)
            .stream()
            .filter(member -> ClassMemberStatus.ACTIVE.name().equals(member.getStatus()))
            .map(member -> {
                User student = member.getStudent();
                Map<Long, ExamAttempt> latest = submitted
                    .stream()
                    .filter(attempt -> Objects.equals(attempt.getStudent().getId(), student.getId()))
                    .filter(attempt ->
                        classExams
                            .stream()
                            .anyMatch(
                                exam ->
                                    Objects.equals(exam.getId(), attempt.getExam().getId()) &&
                                    (exam.getAssignedStudent() == null ||
                                        Objects.equals(exam.getAssignedStudent().getId(), student.getId()))
                            )
                    )
                    .collect(
                        Collectors.toMap(
                            attempt -> attempt.getExam().getId(),
                            attempt -> attempt,
                            (left, right) -> left.getSubmittedAt().isAfter(right.getSubmittedAt()) ? left : right
                        )
                    );
                BigDecimal average = latest.isEmpty()
                    ? BigDecimal.ZERO
                    : latest
                          .values()
                          .stream()
                          .map(ExamAttempt::getScore)
                          .reduce(BigDecimal.ZERO, BigDecimal::add)
                          .divide(BigDecimal.valueOf(latest.size()), 2, RoundingMode.HALF_UP);
                List<ClassExamDTO.Mistake> mistakes = latest
                    .values()
                    .stream()
                    .flatMap(attempt ->
                        answers
                            .findByAttemptId(attempt.getId())
                            .stream()
                            .filter(answer -> !Boolean.TRUE.equals(answer.getIsCorrect()))
                            .map(answer ->
                                new ClassExamDTO.Mistake(
                                    attempt.getExam().getId(),
                                    attempt.getExam().getTitle(),
                                    answer.getQuestion().getContent(),
                                    answer.getOption() == null ? null : answer.getOption().getContent(),
                                    options
                                        .findByQuestionIdOrderByOrderIndexAsc(answer.getQuestion().getId())
                                        .stream()
                                        .filter(option -> Boolean.TRUE.equals(option.getIsCorrect()))
                                        .map(QuestionOption::getContent)
                                        .findFirst()
                                        .orElse(""),
                                    answer.getQuestion().getExplanation()
                                )
                            )
                    )
                    .toList();
                String name = (
                    (student.getFirstName() == null ? "" : student.getFirstName()) +
                    " " +
                    (student.getLastName() == null ? "" : student.getLastName())
                ).trim();
                int assigned = (int) classExams
                    .stream()
                    .filter(exam -> exam.getAssignedStudent() == null || Objects.equals(exam.getAssignedStudent().getId(), student.getId()))
                    .count();
                return new ClassExamDTO.StudentProgress(
                    student.getId(),
                    name.isBlank() ? student.getLogin() : name,
                    latest.size(),
                    assigned,
                    average,
                    mistakes.size(),
                    mistakes
                );
            })
            .toList();
        BigDecimal classAverage = students.isEmpty()
            ? BigDecimal.ZERO
            : students
                  .stream()
                  .map(ClassExamDTO.StudentProgress::averageScore)
                  .reduce(BigDecimal.ZERO, BigDecimal::add)
                  .divide(BigDecimal.valueOf(students.size()), 2, RoundingMode.HALF_UP);
        return new ClassExamDTO.Progress(students, classAverage, classExams.size());
    }
}
