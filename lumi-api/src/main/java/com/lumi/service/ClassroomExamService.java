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
            exam.getAssignedStudent() == null ? null : exam.getAssignedStudent().getId(),
            exam.getAssessmentType(),
            exam.getMaxAttempts(),
            exam.getResultReleaseMode(),
            exam.getAnswerReleaseMode(),
            exam.getOpensAt(),
            exam.getClosesAt(),
            exam.getShuffleQuestions()
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
    public List<ClassExamDTO.StudentExamSummary> studentExams(Long classId) {
        User student = currentUser();
        joinedClass(classId, student);
        Instant now = Instant.now();

        return exams
            .findByStudyClassIdAndIsDeletedFalseOrderByCreatedAtDesc(classId)
            .stream()
            .filter(exam -> {
                // 1. Chỉ lấy đề giao cho cả lớp hoặc giao riêng cho học sinh này
                boolean forStudent =
                    exam.getAssignedStudent() == null || Objects.equals(exam.getAssignedStudent().getId(), student.getId());
                if (!forStudent) return false;

                // 2. Tài liệu đính kèm (nếu có) phải PUBLISHED; đề không gắn tài liệu vẫn hợp lệ
                boolean materialValid = exam.getClassMaterial() == null || "PUBLISHED".equals(exam.getClassMaterial().getStatus());
                if (!materialValid) return false;

                // 3. Kiểm tra xem học sinh đã từng có lượt làm nào chưa
                boolean hasHistory = !attempts.findByExamIdAndStudentIdOrderByStartTimeDesc(exam.getId(), student.getId()).isEmpty();

                // 4. Đề còn hiệu lực để làm
                boolean isActive = exam.getStatus() == RecordStatus.ACTIVE;
                boolean notClosed = exam.getClosesAt() == null || exam.getClosesAt().isAfter(now);

                return hasHistory || (isActive && notClosed);
            })
            .map(exam -> {
                List<ExamAttempt> userAttempts = attempts.findByExamIdAndStudentIdOrderByStartTimeDesc(exam.getId(), student.getId());
                Long inProgressAttemptId = userAttempts
                    .stream()
                    .filter(
                        item ->
                            "IN_PROGRESS".equals(item.getStatus()) && (item.getDeadlineAt() == null || item.getDeadlineAt().isAfter(now))
                    )
                    .map(ExamAttempt::getId)
                    .findFirst()
                    .orElse(null);
                int usedAttempts = userAttempts.size();
                Integer maxAttempts = exam.getMaxAttempts();
                Integer remainingAttempts = maxAttempts == null ? null : Math.max(0, maxAttempts - usedAttempts);

                // Xác định trạng thái
                String status;
                if (exam.getOpensAt() != null && exam.getOpensAt().isAfter(now)) {
                    status = "UPCOMING";
                } else if (exam.getClosesAt() != null && !exam.getClosesAt().isAfter(now)) {
                    status = "CLOSED";
                } else if (exam.getStatus() != RecordStatus.ACTIVE) {
                    status = "CLOSED";
                } else if (inProgressAttemptId == null && maxAttempts != null && usedAttempts >= maxAttempts) {
                    status = "COMPLETED";
                } else {
                    status = "AVAILABLE";
                }

                return new ClassExamDTO.StudentExamSummary(
                    exam.getId(),
                    exam.getClassMaterial() == null ? null : exam.getClassMaterial().getId(),
                    exam.getTitle(),
                    exam.getDurationMinutes(),
                    exam.getPassScore(),
                    (int) examQuestions.countByExamId(exam.getId()),
                    exam.getAssessmentType(),
                    maxAttempts,
                    usedAttempts,
                    remainingAttempts,
                    exam.getOpensAt(),
                    exam.getClosesAt(),
                    status,
                    inProgressAttemptId
                );
            })
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

        if (!input.openAt().isBefore(input.closesAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giờ đóng đề phải sau giờ mở đề");
        }
        if (!Set.of("EXAM", "PRACTICE").contains(input.assessmentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loại bài kiểm tra không hợp lệ");
        }
        if (
            !Set.of("IMMEDIATE", "AFTER_CLOSE").contains(input.resultReleaseMode()) ||
            !Set.of("AFTER_SUBMIT", "AFTER_CLOSE", "NEVER").contains(input.answerReleaseMode())
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cấu hình công bố kết quả không hợp lệ");
        }
        if (input.maxAttempt() != null && (input.maxAttempt() < 1 || input.maxAttempt() > 10)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số lượt làm phải từ 1 đến 10 hoặc không giới hạn");
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
                .maxAttempts(input.maxAttempt())
                .shuffleQuestions(input.shuffleQuestions())
                .opensAt(input.openAt())
                .closesAt(input.closesAt())
                .status(RecordStatus.ACTIVE)
                .isDeleted(false)
                .resultReleaseMode(input.resultReleaseMode())
                .answerReleaseMode(input.answerReleaseMode())
                .version(1L)
                .createdAt(Instant.now())
                .assessmentType(input.assessmentType())
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

    @Transactional(readOnly = true)
    public ClassExamDTO.AssessmentRulesConfig getAssessmentRuleDefaults(String type) {
        String assessmentType = type != null && type.equalsIgnoreCase("PRACTICE") ? "PRACTICE" : "EXAM";

        if ("PRACTICE".equals(assessmentType)) {
            return new ClassExamDTO.AssessmentRulesConfig(
                "PRACTICE",
                0,
                10,
                true,
                0,
                false,
                50,
                "IMMEDIATE",
                "AFTER_SUBMIT",
                List.of("IMMEDIATE"),
                List.of("AFTER_SUBMIT")
            );
        }

        // Mặc định là EXAM (Bài kiểm tra)
        return new ClassExamDTO.AssessmentRulesConfig(
            "EXAM",
            1,
            3,
            false,
            15,
            true,
            50,
            "IMMEDIATE",
            "AFTER_SUBMIT",
            List.of("IMMEDIATE", "AFTER_CLOSE"),
            List.of("AFTER_SUBMIT", "AFTER_CLOSE", "NEVER")
        );
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
        if (!input.openAt().isBefore(input.closesAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giờ đóng đề phải sau giờ mở đề");
        }
        if (
            attempts.existsByExamId(examId) &&
            (input.questions() != null ||
                !Objects.equals(exam.getDurationMinutes(), input.durationMinutes()) ||
                exam.getPassScore().compareTo(input.passScore()) != 0 ||
                (exam.getAssessmentType() != null && !Objects.equals(exam.getAssessmentType(), input.assessmentType())) ||
                (exam.getResultReleaseMode() != null && !Objects.equals(exam.getResultReleaseMode(), input.resultReleaseMode())) ||
                (exam.getAnswerReleaseMode() != null && !Objects.equals(exam.getAnswerReleaseMode(), input.answerReleaseMode())) ||
                !Objects.equals(exam.getMaxAttempts(), input.maxAttempt()) ||
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
        exam.setAssessmentType(input.assessmentType());
        exam.setMaxAttempts(input.maxAttempt());
        exam.setResultReleaseMode(input.resultReleaseMode());
        exam.setAnswerReleaseMode(input.answerReleaseMode());
        exam.setShuffleQuestions(Boolean.TRUE.equals(input.shuffleQuestions()));
        exam.setOpensAt(input.openAt());
        exam.setClosesAt(input.closesAt());
        exam.setVersion(exam.getVersion() + 1);
        return summary(exams.save(exam));
    }

    public ClassExamDTO.Summary updateStatus(Long classId, Long examId, ClassExamDTO.UpdateStatus input) {
        User instructor = currentUser();
        ownedClass(classId, instructor);
        Exam exam = classExam(classId, examId);
        if (!Set.of("ACTIVE", "ARCHIVED").contains(input.status())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trạng thái bài kiểm tra không hợp lệ");
        }
        exam.setStatus(RecordStatus.valueOf(input.status()));
        exam.setVersion(exam.getVersion() + 1);
        return summary(exams.save(exam));
    }

    public ClassExamDTO.AttemptView startAttempt(Long classId, Long examId) {
        User student = currentUser();
        joinedClass(classId, student);

        // Khóa bi quan để chống tạo 2 lượt cùng lúc khi học sinh click nhanh
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

        // Kiểm tra quyền và thời gian mở đề
        if (
            exam.getStatus() != RecordStatus.ACTIVE ||
            (exam.getAssignedStudent() != null && !Objects.equals(exam.getAssignedStudent().getId(), student.getId())) ||
            (exam.getOpensAt() != null && exam.getOpensAt().isAfter(now)) ||
            (exam.getClosesAt() != null && !exam.getClosesAt().isAfter(now)) ||
            (exam.getClassMaterial() != null && !"PUBLISHED".equals(exam.getClassMaterial().getStatus()))
        ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bài kiểm tra chưa mở hoặc đã kết thúc");
        }

        List<ExamAttempt> previous = attempts.findByExamIdAndStudentIdOrderByStartTimeDesc(examId, student.getId());

        for (ExamAttempt previousAttempt : previous) {
            if (
                "IN_PROGRESS".equals(previousAttempt.getStatus()) &&
                previousAttempt.getDeadlineAt() != null &&
                !now.isBefore(previousAttempt.getDeadlineAt())
            ) {
                ExamAttempt locked = attempts.findByIdForUpdate(previousAttempt.getId()).orElseThrow();
                if ("IN_PROGRESS".equals(locked.getStatus())) {
                    completeAttempt(locked, exam, null, true, now);
                }
            }
        }

        // Kiểm tra xem có lượt nào đang làm dở (IN_PROGRESS) và còn trong thời hạn không
        ExamAttempt attempt = previous
            .stream()
            .filter(item -> "IN_PROGRESS".equals(item.getStatus()) && (item.getDeadlineAt() == null || item.getDeadlineAt().isAfter(now)))
            .findFirst()
            .orElse(null);

        // Nếu chưa có lượt dở thì tạo lượt mới
        if (attempt == null) {
            if (exam.getMaxAttempts() != null && previous.size() >= exam.getMaxAttempts()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Bạn đã dùng hết số lần làm bài");
            }

            // Tính deadline chuẩn
            Instant deadline = null;
            if (exam.getDurationMinutes() != null && exam.getDurationMinutes() > 0) {
                deadline = now.plusSeconds(exam.getDurationMinutes() * 60L);
                // Deadline không được vượt quá thời điểm đóng đề
                if (exam.getClosesAt() != null && deadline.isAfter(exam.getClosesAt())) {
                    deadline = exam.getClosesAt();
                }
            } else if (exam.getClosesAt() != null) {
                deadline = exam.getClosesAt();
            }

            attempt = attempts.save(
                new ExamAttempt()
                    .exam(exam)
                    .student(student)
                    .startTime(now)
                    .deadlineAt(deadline)
                    .status("IN_PROGRESS")
                    .rowVersion(0L)
                    .monitoringStatus("NONE")
            );
        }

        // Lấy danh sách câu hỏi (chỉ lấy nội dung, điểm và lựa chọn, không có đáp án đúng)
        List<ClassExamDTO.QuestionView> views = new ArrayList<>(
            examQuestions
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
                .toList()
        );

        // Đảo câu hỏi nếu giáo viên bật shuffleQuestions
        if (Boolean.TRUE.equals(exam.getShuffleQuestions())) {
            Collections.shuffle(views, new java.util.Random(attempt.getId()));
        }

        // Lấy danh sách câu trả lời đã lưu trước đó của lượt này
        List<ClassExamDTO.AnswerInput> savedAnswers = answers
            .findByAttemptId(attempt.getId())
            .stream()
            .map(ans -> new ClassExamDTO.AnswerInput(ans.getQuestion().getId(), ans.getOption() == null ? null : ans.getOption().getId()))
            .toList();

        // Tính số lượt còn lại sau lượt này
        int totalUsed = previous.contains(attempt) ? previous.size() : previous.size() + 1;
        Integer remaining = exam.getMaxAttempts() == null ? null : Math.max(0, exam.getMaxAttempts() - totalUsed);

        return new ClassExamDTO.AttemptView(attempt.getId(), examId, attempt.getDeadlineAt(), views, savedAnswers, remaining);
    }

    public ClassExamDTO.Result submit(Long classId, Long examId, Long attemptId, ClassExamDTO.SubmitExam input) {
        User student = currentUser();
        joinedClass(classId, student);
        Exam exam = classExam(classId, examId);
        ExamAttempt attempt = attempts
            .findByIdForUpdate(attemptId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lượt làm bài"));
        if (!Objects.equals(attempt.getExam().getId(), examId) || !Objects.equals(attempt.getStudent().getId(), student.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lượt làm bài");
        }
        if (isCompleted(attempt)) {
            return studentResultView(attempt, exam);
        }
        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lượt làm bài không còn ở trạng thái đang làm");
        }
        Instant now = Instant.now();
        boolean expired = attempt.getDeadlineAt() == null || !now.isBefore(attempt.getDeadlineAt());
        List<ClassExamDTO.AnswerInput> submittedAnswers = expired || input == null ? null : input.answers();
        completeAttempt(attempt, exam, submittedAnswers, expired, now);
        return studentResultView(attempt, exam);
    }

    private boolean isCompleted(ExamAttempt attempt) {
        return "SUBMITTED".equals(attempt.getStatus()) || "AUTO_SUBMITTED".equals(attempt.getStatus());
    }

    private void completeAttempt(
        ExamAttempt attempt,
        Exam exam,
        List<ClassExamDTO.AnswerInput> submittedAnswers,
        boolean autoSubmitted,
        Instant now
    ) {
        List<ExamQuestion> examItems = examQuestions.findByExamIdOrderByOrderIndexAsc(exam.getId());
        Map<Long, ExamQuestion> examItemsByQuestion = new HashMap<>();
        for (ExamQuestion item : examItems) {
            examItemsByQuestion.put(item.getQuestion().getId(), item);
        }

        List<ExamAnswer> savedRows = answers.findByAttemptId(attempt.getId());
        Map<Long, ExamAnswer> savedByQuestion = new HashMap<>();
        for (ExamAnswer row : savedRows) {
            if (savedByQuestion.put(row.getQuestion().getId(), row) != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Dữ liệu đáp án của lượt làm bị trùng");
            }
        }

        Map<Long, Long> selectedIds = new HashMap<>();
        if (submittedAnswers == null) {
            for (ExamAnswer row : savedRows) {
                selectedIds.put(row.getQuestion().getId(), row.getOption() == null ? null : row.getOption().getId());
            }
        } else {
            if (submittedAnswers.size() > examItemsByQuestion.size()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số đáp án vượt quá số câu của đề");
            }
            for (ClassExamDTO.AnswerInput answer : submittedAnswers) {
                if (!examItemsByQuestion.containsKey(answer.questionId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Câu hỏi không thuộc đề");
                }
                if (selectedIds.containsKey(answer.questionId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Câu hỏi được trả lời nhiều lần");
                }
                selectedIds.put(answer.questionId(), answer.optionId());
            }
        }

        Map<Long, QuestionOption> selectedOptions = new HashMap<>();
        BigDecimal earned = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        for (ExamQuestion item : examItems) {
            Long questionId = item.getQuestion().getId();
            Long optionId = selectedIds.get(questionId);
            QuestionOption selected = null;
            if (optionId != null) {
                selected = options
                    .findByQuestionIdOrderByOrderIndexAsc(questionId)
                    .stream()
                    .filter(option -> Objects.equals(option.getId(), optionId))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lựa chọn không thuộc câu hỏi"));
            }
            selectedOptions.put(questionId, selected);
            total = total.add(item.getScore());
            if (selected != null && Boolean.TRUE.equals(selected.getIsCorrect())) {
                earned = earned.add(item.getScore());
            }
        }

        List<ExamAnswer> gradedRows = new ArrayList<>();
        for (ExamQuestion item : examItems) {
            Long questionId = item.getQuestion().getId();
            QuestionOption selected = selectedOptions.get(questionId);
            boolean correct = selected != null && Boolean.TRUE.equals(selected.getIsCorrect());
            ExamAnswer row = savedByQuestion.get(questionId);
            if (row == null) {
                row = new ExamAnswer().attempt(attempt).question(item.getQuestion()).rowVersion(0L);
            } else {
                row.setRowVersion((row.getRowVersion() == null ? 0L : row.getRowVersion()) + 1);
            }
            row.setOption(selected);
            row.setIsCorrect(correct);
            row.setScore(correct ? item.getScore() : BigDecimal.ZERO);
            row.setUpdatedAt(now);
            gradedRows.add(row);
        }
        answers.saveAll(gradedRows);

        attempt.setScore(
            total.signum() == 0 ? BigDecimal.ZERO : earned.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP)
        );
        attempt.setStatus(autoSubmitted ? "AUTO_SUBMITTED" : "SUBMITTED");
        attempt.setSubmittedAt(now);
        attempt.setEndTime(now);
        attempt.setRowVersion((attempt.getRowVersion() == null ? 0L : attempt.getRowVersion()) + 1);
        attempts.save(attempt);
    }

    @Transactional
    public ClassExamDTO.SaveDraftResult saveDraftAnswers(Long classId, Long examId, Long attemptId, ClassExamDTO.Submit input) {
        User student = currentUser();
        joinedClass(classId, student);
        Exam exam = classExam(classId, examId);
        var now = Instant.now();
        ExamAttempt attempt = attempts
            .findByIdForUpdate(attemptId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lượt làm bài"));

        if (attempt.getStatus().equals("SUBMITTED")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn lưu được bản nháp vì phần thi đã hoàn thành");
        }
        if (!Objects.equals(attempt.getExam().getId(), examId) || !Objects.equals(attempt.getStudent().getId(), student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn lưu được bản nháp vì phần thi không phải của bạn");
        }
        if (attempt.getDeadlineAt().isBefore(now)) throw new ResponseStatusException(
            HttpStatus.FORBIDDEN,
            "Bạn lưu được bản nháp vì phần thi đã kết thúc"
        );

        // lấy danh sách câu bộ câu hỏi
        List<ExamQuestion> examItems = examQuestions.findByExamIdOrderByOrderIndexAsc(examId);
        Set<Long> allowedQuestionIds = examItems
            .stream()
            .map(item -> item.getQuestion().getId())
            .collect(Collectors.toSet());

        if (input.answers().size() > allowedQuestionIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quá số câu của đề");
        }

        // kiểm tra câu hỏi có bị trùng hay không
        Set<Long> seen = new HashSet<>();

        for (ClassExamDTO.AnswerInput answer : input.answers()) {
            if (!seen.add(answer.questionId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "questionId bị trùng");
            }
            if (!allowedQuestionIds.contains(answer.questionId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Câu hỏi không thuộc đề");
            }
        }
        // kiểm tra option có thuộc question không
        Map<Long, QuestionOption> selectedOptions = new HashMap<>();

        for (ClassExamDTO.AnswerInput answerInput : input.answers()) {
            if (answerInput.optionId() == null) {
                continue;
            }

            QuestionOption selected = options
                .findByQuestionIdOrderByOrderIndexAsc(answerInput.questionId())
                .stream()
                .filter(option -> Objects.equals(option.getId(), answerInput.optionId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lựa chọn không thuộc câu hỏi"));

            selectedOptions.put(answerInput.questionId(), selected);
        }

        // lấy bản nháp đã lưu ở trong exam_answers
        List<ExamAnswer> existingAnswers = answers.findByAttemptId(attemptId);
        Map<Long, ExamAnswer> existingByQuestion = new HashMap<>();

        for (ExamAnswer existing : existingAnswers) {
            Long questionId = existing.getQuestion().getId();

            if (existingByQuestion.put(questionId, existing) != null) {
                // Trường hợp DB đã có dữ liệu trùng từ trước.
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Dữ liệu đáp án của lượt làm bị trùng");
            }
        }

        // 8. Câu không còn trong request PUT sẽ bị xóa.
        List<ExamAnswer> rowsToDelete = existingAnswers
            .stream()
            .filter(existing -> !seen.contains(existing.getQuestion().getId()))
            .toList();

        // 9. Câu có trong request được cập nhật hoặc tạo mới.
        List<ExamAnswer> rowsToSave = new ArrayList<>();

        for (ClassExamDTO.AnswerInput answerInput : input.answers()) {
            Long questionId = answerInput.questionId();

            ExamAnswer row = existingByQuestion.get(questionId);

            if (row == null) {
                row = new ExamAnswer()
                    .attempt(attempt)
                    .question(questions.findById(questionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .rowVersion(0L);
            } else {
                long answerVersion = row.getRowVersion() == null ? 0L : row.getRowVersion();

                row.setRowVersion(answerVersion + 1);
            }

            row.setOption(selectedOptions.get(questionId));
            row.setUpdatedAt(now);

            // Chỉ lưu bản nháp; chưa chấm điểm.
            row.setIsCorrect(null);
            row.setScore(null);

            rowsToSave.add(row);
        }

        answers.deleteAll(rowsToDelete);
        answers.saveAll(rowsToSave);

        // 10. Cập nhật metadata của lượt làm.
        attempt.setLastSavedAt(now);
        attempts.save(attempt);

        return new ClassExamDTO.SaveDraftResult(rowsToSave.size(), now);
    }

    @Transactional
    public ClassExamDTO.Result studentResult(Long classId, Long examId, Long attemptId) {
        User student = currentUser();
        joinedClass(classId, student);
        Exam exam = classExam(classId, examId);
        ExamAttempt attempt = attempts.findByIdForUpdate(attemptId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!Objects.equals(attempt.getExam().getId(), examId) || !Objects.equals(attempt.getStudent().getId(), student.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lượt làm bài");
        }
        if ("IN_PROGRESS".equals(attempt.getStatus())) {
            Instant now = Instant.now();
            if (attempt.getDeadlineAt() != null && !now.isBefore(attempt.getDeadlineAt())) {
                completeAttempt(attempt, exam, null, true, now);
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Bài làm chưa hoàn tất");
            }
        }
        if (!isCompleted(attempt)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bài làm chưa có kết quả");
        }
        return studentResultView(attempt, exam);
    }

    @Transactional
    public List<ClassExamDTO.Result> studentResults(Long classId, Long examId) {
        User student = currentUser();
        joinedClass(classId, student);
        Exam exam = classExam(classId, examId);
        List<ClassExamDTO.Result> results = new ArrayList<>();
        for (ExamAttempt item : attempts.findByExamIdAndStudentIdOrderByStartTimeDesc(examId, student.getId())) {
            ExamAttempt attempt = item;
            if ("IN_PROGRESS".equals(item.getStatus())) {
                Instant now = Instant.now();
                if (item.getDeadlineAt() != null && !now.isBefore(item.getDeadlineAt())) {
                    attempt = attempts.findByIdForUpdate(item.getId()).orElseThrow();
                    if ("IN_PROGRESS".equals(attempt.getStatus())) {
                        completeAttempt(attempt, exam, null, true, now);
                    }
                }
            }
            if (isCompleted(attempt)) {
                results.add(studentResultView(attempt, exam));
            }
        }
        return results;
    }

    private ClassExamDTO.Result studentResultView(ExamAttempt attempt, Exam exam) {
        Instant now = Instant.now();
        Instant resultAvailableAt = switch (exam.getResultReleaseMode()) {
            case "IMMEDIATE" -> attempt.getSubmittedAt();
            case "AFTER_CLOSE" -> exam.getClosesAt();
            default -> throw new ResponseStatusException(HttpStatus.CONFLICT, "Cấu hình công bố điểm không hợp lệ");
        };
        if (resultAvailableAt == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bài thi chưa có thời điểm công bố điểm");
        }
        boolean scoreVisible = !now.isBefore(resultAvailableAt);
        Instant answersAvailableAt = switch (exam.getAnswerReleaseMode()) {
            case "AFTER_SUBMIT" -> attempt.getSubmittedAt();
            case "AFTER_CLOSE" -> exam.getClosesAt();
            case "NEVER" -> null;
            default -> throw new ResponseStatusException(HttpStatus.CONFLICT, "Cấu hình công bố đáp án không hợp lệ");
        };
        boolean feedbackVisible = scoreVisible && answersAvailableAt != null && !now.isBefore(answersAvailableAt);
        List<ClassExamDTO.Feedback> feedback = feedbackVisible ? buildStudentFeedback(attempt) : List.of();
        return new ClassExamDTO.Result(
            attempt.getId(),
            attempt.getStatus(),
            scoreVisible ? attempt.getScore() : null,
            scoreVisible ? attempt.getScore().compareTo(exam.getPassScore()) >= 0 : null,
            attempt.getSubmittedAt(),
            resultAvailableAt,
            answersAvailableAt,
            feedback
        );
    }

    private List<ClassExamDTO.Feedback> buildStudentFeedback(ExamAttempt attempt) {
        return answers
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
                    answer.getQuestion().getExplanation(),
                    choices.stream().map(QuestionOption::getContent).toList()
                );
            })
            .toList();
    }

    @Transactional(readOnly = true)
    public ClassExamDTO.Progress progress(Long classId) {
        ownedClass(classId, currentUser());
        List<Exam> classExams = exams.findByStudyClassIdAndIsDeletedFalseOrderByCreatedAtDesc(classId);
        List<ExamAttempt> submitted = attempts.findByExamStudyClassIdAndStatusIn(classId, List.of("SUBMITTED", "AUTO_SUBMITTED"));
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
        List<ClassExamDTO.StudentProgress> completedStudents = students
            .stream()
            .filter(student -> student.completedExams() > 0)
            .toList();
        BigDecimal classAverage = completedStudents.isEmpty()
            ? BigDecimal.ZERO
            : completedStudents
                  .stream()
                  .map(ClassExamDTO.StudentProgress::averageScore)
                  .reduce(BigDecimal.ZERO, BigDecimal::add)
                  .divide(BigDecimal.valueOf(completedStudents.size()), 2, RoundingMode.HALF_UP);
        return new ClassExamDTO.Progress(students, classAverage, classExams.size());
    }
}
