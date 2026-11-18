package com.lumi.service.impl;

import com.lumi.domain.*;
import com.lumi.domain.enumeration.InstructorApplicationStatus;
import com.lumi.domain.enumeration.RecordStatus;
import com.lumi.repository.*;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.security.SecurityUtils;
import com.lumi.service.dto.InstructorExamDTOs.*;
import com.lumi.service.mapper.ExamMapper;
import com.lumi.service.util.InstructorCourseHelper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class InstructorCourseExamServiceImpl {

    private final CourseRepository courses;
    private final ChapterRepository chapters;
    private final LessonRepository lessons;
    private final ExamRepository exams;
    private final ExamQuestionRepository examQuestions;
    private final QuestionRepository questions;
    private final QuestionOptionRepository questionOptions;
    private final UserRepository users;
    private final UserProfileRepository profiles;
    private final ExamMapper examMapper;

    public InstructorCourseExamServiceImpl(
        CourseRepository courses,
        ChapterRepository chapters,
        LessonRepository lessons,
        ExamRepository exams,
        ExamQuestionRepository examQuestions,
        QuestionRepository questions,
        QuestionOptionRepository questionOptions,
        UserRepository users,
        UserProfileRepository profiles,
        ExamMapper examMapper
    ) {
        this.courses = courses;
        this.chapters = chapters;
        this.lessons = lessons;
        this.exams = exams;
        this.examQuestions = examQuestions;
        this.questions = questions;
        this.questionOptions = questionOptions;
        this.users = users;
        this.profiles = profiles;
        this.examMapper = examMapper;
    }

    private User currentInstructor() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new AccessDeniedException("Authentication required"));
        User user = users.findOneByLogin(login).orElseThrow(() -> new AccessDeniedException("Account not found"));
        boolean admin = SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN);
        boolean reviewer = SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.REVIEWER);
        boolean instructor = profiles
            .findOneByUserId(user.getId())
            .map(p -> p.getInstructorStatus() == InstructorApplicationStatus.ACTIVE)
            .orElse(false);
        if (!user.isActivated() || (!admin && !reviewer && !instructor)) {
            throw new AccessDeniedException("Active instructor required");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public Course ownedCourse(Long courseId) {
        User user = currentInstructor();
        boolean admin = SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN);
        boolean reviewer = SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.REVIEWER);
        Course course = courses.findById(courseId).orElseThrow(() -> InstructorCourseHelper.missing("Course"));
        if (Boolean.TRUE.equals(course.getIsDeleted())) {
            throw InstructorCourseHelper.missing("Course");
        }
        if (Boolean.TRUE.equals(course.getIsSystemCourse())) {
            if (!admin && !reviewer && !course.getAuthor().getId().equals(user.getId())) {
                throw InstructorCourseHelper.missing("Course");
            }
        } else if (!admin && !course.getAuthor().getId().equals(user.getId())) {
            throw InstructorCourseHelper.missing("Course");
        }
        return course;
    }

    @Transactional(readOnly = true)
    public Chapter ownedChapter(Long courseId, Long chapterId) {
        Course course = ownedCourse(courseId);
        Chapter chapter = chapters.findById(chapterId).orElseThrow(() -> InstructorCourseHelper.missing("Chapter"));
        if (!chapter.getCourse().getId().equals(course.getId())) {
            throw InstructorCourseHelper.missing("Chapter");
        }
        return chapter;
    }

    @Transactional(readOnly = true)
    public Lesson ownedLesson(Long courseId, Long lessonId) {
        Course course = ownedCourse(courseId);
        Lesson lesson = lessons.findById(lessonId).orElseThrow(() -> InstructorCourseHelper.missing("Lesson"));
        if (!lesson.getChapter().getCourse().getId().equals(course.getId())) {
            throw InstructorCourseHelper.missing("Lesson");
        }
        return lesson;
    }

    @Transactional(readOnly = true)
    public Exam ownedExam(Long courseId, Long examId) {
        Course course = ownedCourse(courseId);
        Exam exam = exams.findByIdAndIsDeletedFalse(examId).orElseThrow(() -> InstructorCourseHelper.missing("Exam"));

        boolean match = false;
        if (exam.getCourse() != null && exam.getCourse().getId().equals(course.getId())) {
            match = true;
        } else if (exam.getChapter() != null && exam.getChapter().getCourse().getId().equals(course.getId())) {
            match = true;
        } else if (exam.getLesson() != null && exam.getLesson().getChapter().getCourse().getId().equals(course.getId())) {
            match = true;
        }

        if (!match) {
            throw InstructorCourseHelper.missing("Exam");
        }
        return exam;
    }

    // =========================================================================
    // 1. Course-level Exams (Đề kiểm tra khóa học)
    // =========================================================================

    @Transactional(readOnly = true)
    public List<ExamSummaryDTO> listCourseExams(Long courseId) {
        ownedCourse(courseId);
        List<Exam> list = exams.findByCourseIdAndChapterIsNullAndLessonIsNullAndIsDeletedFalseOrderByCreatedAtDesc(courseId);
        return list.stream().map(examMapper::toSummaryDto).toList();
    }

    public ExamDetailDTO createCourseExam(Long courseId, CreateExamInput input) {
        Course course = ownedCourse(courseId);
        User author = currentInstructor();

        Exam exam = InstructorCourseHelper.initBaseExam(input, "EXAM", author);
        exam.setCourse(course);
        exams.saveAndFlush(exam);

        return examMapper.toDetailDto(exam);
    }

    // =========================================================================
    // 2. Chapter-level Quizzes (Bài kiểm tra chương)
    // =========================================================================

    @Transactional(readOnly = true)
    public List<ExamSummaryDTO> listChapterExams(Long courseId, Long chapterId) {
        ownedChapter(courseId, chapterId);
        List<Exam> list = exams.findByChapterIdAndLessonIsNullAndIsDeletedFalseOrderByCreatedAtDesc(chapterId);
        return list.stream().map(examMapper::toSummaryDto).toList();
    }

    public ExamDetailDTO createChapterExam(Long courseId, Long chapterId, CreateExamInput input) {
        Chapter chapter = ownedChapter(courseId, chapterId);
        User author = currentInstructor();

        Exam exam = InstructorCourseHelper.initBaseExam(input, "CHAPTER_QUIZ", author);
        exam.setCourse(chapter.getCourse());
        exam.setChapter(chapter);
        exams.saveAndFlush(exam);

        return examMapper.toDetailDto(exam);
    }

    // =========================================================================
    // 3. Lesson-level Quizzes (Bài tập ôn tập cuối bài học)
    // =========================================================================

    @Transactional(readOnly = true)
    public List<ExamSummaryDTO> listLessonQuizzes(Long courseId, Long lessonId) {
        ownedLesson(courseId, lessonId);
        List<Exam> list = exams.findByLessonIdAndIsDeletedFalseOrderByCreatedAtDesc(lessonId);
        return list.stream().map(examMapper::toSummaryDto).toList();
    }

    public ExamDetailDTO createLessonQuiz(Long courseId, Long lessonId, CreateExamInput input, Boolean isPractice) {
        Lesson lesson = ownedLesson(courseId, lessonId);
        User author = currentInstructor();

        String assessmentType = Boolean.TRUE.equals(isPractice) ? "PRACTICE" : "LESSON_QUIZ";
        Exam exam = InstructorCourseHelper.initBaseExam(input, assessmentType, author);
        exam.setCourse(lesson.getChapter().getCourse());
        exam.setChapter(lesson.getChapter());
        exam.setLesson(lesson);
        exams.saveAndFlush(exam);

        return examMapper.toDetailDto(exam);
    }

    // =========================================================================
    // 4. Video Cue-Points (Câu hỏi tương tác dừng video)
    // =========================================================================

    @Transactional(readOnly = true)
    public List<ExamSummaryDTO> listLessonCuePoints(Long courseId, Long lessonId) {
        ownedLesson(courseId, lessonId);
        List<Exam> list = exams.findByLessonIdAndAssessmentTypeAndIsDeletedFalseOrderByVideoTriggerSecondsAsc(lessonId, "VIDEO_CHECKPOINT");
        return list.stream().map(examMapper::toSummaryDto).toList();
    }

    public ExamDetailDTO createLessonCuePoint(Long courseId, Long lessonId, CreateExamInput input) {
        Lesson lesson = ownedLesson(courseId, lessonId);
        if (input.videoTriggerSeconds() == null || input.videoTriggerSeconds().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "videoTriggerSeconds is required for cue-points");
        }
        boolean exists = exams.existsByLessonIdAndAssessmentTypeAndVideoTriggerSecondsAndIsDeletedFalse(
            lessonId,
            "VIDEO_CHECKPOINT",
            input.videoTriggerSeconds()
        );
        if (exists) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Thời điểm " + input.videoTriggerSeconds() + "s đã có câu hỏi tương tác. Vui lòng chọn thời điểm khác."
            );
        }
        User author = currentInstructor();

        Exam exam = InstructorCourseHelper.initBaseExam(input, "VIDEO_CHECKPOINT", author);
        exam.setCourse(lesson.getChapter().getCourse());
        exam.setChapter(lesson.getChapter());
        exam.setLesson(lesson);
        exam.setVideoTriggerSeconds(input.videoTriggerSeconds());
        exams.saveAndFlush(exam);

        return examMapper.toDetailDto(exam);
    }

    // =========================================================================
    // 5. Exam General CRUD (Detail, Update, Delete)
    // =========================================================================

    @Transactional(readOnly = true)
    public ExamDetailDTO getExamDetail(Long courseId, Long examId) {
        Exam exam = ownedExam(courseId, examId);
        return examMapper.toDetailDto(exam);
    }

    public ExamDetailDTO updateExam(Long courseId, Long examId, UpdateExamInput input) {
        Exam exam = ownedExam(courseId, examId);

        exam.setTitle(input.title().trim());
        if (input.durationMinutes() != null) {
            exam.setDurationMinutes(input.durationMinutes());
        }
        if (input.passScore() != null) {
            exam.setPassScore(input.passScore());
        }
        if (input.maxAttempts() != null) {
            exam.setMaxAttempts(input.maxAttempts());
        }
        if (input.shuffleQuestions() != null) {
            exam.setShuffleQuestions(input.shuffleQuestions());
        }
        if ("VIDEO_CHECKPOINT".equals(exam.getAssessmentType()) && input.videoTriggerSeconds() != null) {
            exam.setVideoTriggerSeconds(input.videoTriggerSeconds());
        }

        exam.setVersion(exam.getVersion() + 1);
        exams.saveAndFlush(exam);

        return examMapper.toDetailDto(exam);
    }

    public void deleteExam(Long courseId, Long examId) {
        Exam exam = ownedExam(courseId, examId);
        exam.setIsDeleted(true);
        exam.setStatus(RecordStatus.ARCHIVED);
        exam.setVersion(exam.getVersion() + 1);
        exams.saveAndFlush(exam);
    }

    // =========================================================================
    // 6. Question & Option Management inside Exam
    // =========================================================================

    public ExamDetailDTO addQuestionToExam(Long courseId, Long examId, QuestionInput input) {
        Exam exam = ownedExam(courseId, examId);
        User author = currentInstructor();

        InstructorCourseHelper.validateOptions(input.options());

        Question question = new Question();
        question.setContent(input.content().trim());
        question.setType(input.type() != null ? input.type() : "MCQ");
        question.setDifficulty(input.getNumericDifficulty());
        question.setExplanation(input.explanation());
        question.setSourceType("MANUAL");
        question.setVisibility("PRIVATE");
        question.setVersion(0L);
        question.setIsDeleted(false);
        question.setCreatedBy(author);
        question.setCreatedAt(Instant.now());
        question.setUpdatedAt(Instant.now());
        questions.saveAndFlush(question);

        InstructorCourseHelper.saveOptions(question, input.options(), questionOptions);

        ExamQuestion eq = new ExamQuestion();
        eq.setExam(exam);
        eq.setQuestion(question);
        eq.setScore(input.score() != null ? input.score() : BigDecimal.ONE);
        eq.setOrderIndex(input.orderIndex() != null ? input.orderIndex() : 0);
        examQuestions.saveAndFlush(eq);

        return examMapper.toDetailDto(exam);
    }

    public ExamDetailDTO updateQuestionInExam(Long courseId, Long examId, Long questionId, QuestionInput input) {
        Exam exam = ownedExam(courseId, examId);

        Question question = questions.findById(questionId).orElseThrow(() -> InstructorCourseHelper.missing("Question"));
        InstructorCourseHelper.validateOptions(input.options());

        question.setContent(input.content().trim());
        if (input.type() != null) question.setType(input.type());
        if (input.difficulty() != null) question.setDifficulty(input.getNumericDifficulty());
        question.setExplanation(input.explanation());
        question.setUpdatedAt(Instant.now());
        question.setVersion(question.getVersion() + 1);
        questions.saveAndFlush(question);

        // Update options: delete old, insert new
        questionOptions.deleteByQuestionId(question.getId());
        InstructorCourseHelper.saveOptions(question, input.options(), questionOptions);

        // Update ExamQuestion score/orderIndex if provided
        List<ExamQuestion> eqList = examQuestions.findByExamIdOrderByOrderIndexAsc(exam.getId());
        for (ExamQuestion eq : eqList) {
            if (eq.getQuestion().getId().equals(questionId)) {
                if (input.score() != null) eq.setScore(input.score());
                if (input.orderIndex() != null) eq.setOrderIndex(input.orderIndex());
                examQuestions.saveAndFlush(eq);
                break;
            }
        }

        return examMapper.toDetailDto(exam);
    }

    public ExamDetailDTO deleteQuestionFromExam(Long courseId, Long examId, Long questionId) {
        Exam exam = ownedExam(courseId, examId);

        examQuestions.deleteByExamIdAndQuestionId(examId, questionId);
        return examMapper.toDetailDto(exam);
    }
}
