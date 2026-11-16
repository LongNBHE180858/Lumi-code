package com.lumi.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.lumi.domain.*;
import com.lumi.domain.enumeration.InstructorApplicationStatus;
import com.lumi.domain.enumeration.RecordStatus;
import com.lumi.repository.*;
import com.lumi.service.dto.InstructorExamDTOs.*;
import com.lumi.service.impl.InstructorCourseExamService;
import com.lumi.service.mapper.ExamMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class InstructorCourseExamServiceTest {

    @Mock
    CourseRepository courses;

    @Mock
    ChapterRepository chapters;

    @Mock
    LessonRepository lessons;

    @Mock
    ExamRepository exams;

    @Mock
    ExamQuestionRepository examQuestions;

    @Mock
    QuestionRepository questions;

    @Mock
    QuestionOptionRepository questionOptions;

    @Mock
    UserRepository users;

    @Mock
    UserProfileRepository profiles;

    @Mock
    ExamMapper examMapper;

    @Spy
    @InjectMocks
    InstructorCourseExamService service;

    private User teacher;
    private Course course;
    private Chapter chapter;
    private Lesson lesson;

    @BeforeEach
    void setup() {
        teacher = new User();
        teacher.setId(10L);
        teacher.setLogin("instructor1");
        teacher.setActivated(true);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(
            new UsernamePasswordAuthenticationToken("instructor1", "pass", List.of(new SimpleGrantedAuthority("ROLE_USER")))
        );
        SecurityContextHolder.setContext(context);

        lenient().when(users.findOneByLogin("instructor1")).thenReturn(Optional.of(teacher));
        UserProfile profile = new UserProfile();
        profile.setInstructorStatus(InstructorApplicationStatus.ACTIVE);
        lenient().when(profiles.findOneByUserId(10L)).thenReturn(Optional.of(profile));

        course = new Course().id(1L).author(teacher).status(RecordStatus.DRAFT);
        course.setIsDeleted(false);
        chapter = new Chapter().id(20L).course(course).title("Chương 1");
        lesson = new Lesson().id(300L).chapter(chapter).title("Bài 1");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void listCourseExams_Success() {
        doReturn(course).when(service).ownedCourse(1L);
        Exam exam = new Exam().id(50L).title("Final Exam");
        when(exams.findByCourseIdAndChapterIsNullAndLessonIsNullAndIsDeletedFalseOrderByCreatedAtDesc(1L)).thenReturn(List.of(exam));
        ExamSummaryDTO summary = new ExamSummaryDTO(
            50L,
            "Final Exam",
            "EXAM",
            60,
            BigDecimal.valueOf(80),
            1,
            true,
            null,
            1L,
            null,
            null,
            10,
            BigDecimal.valueOf(100),
            null
        );
        when(examMapper.toSummaryDto(exam)).thenReturn(summary);

        List<ExamSummaryDTO> result = service.listCourseExams(1L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Final Exam");
    }

    @Test
    void createCourseExam_Success_Normal() {
        doReturn(course).when(service).ownedCourse(1L);
        CreateExamInput input = new CreateExamInput("Đề thi cuối khóa", 90, BigDecimal.valueOf(70.0), 2, true, null);
        ExamDetailDTO detail = new ExamDetailDTO(
            100L,
            "Đề thi cuối khóa",
            "EXAM",
            90,
            BigDecimal.valueOf(70.0),
            2,
            true,
            null,
            1L,
            null,
            null,
            List.of(),
            null
        );
        when(examMapper.toDetailDto(any(Exam.class))).thenReturn(detail);

        ExamDetailDTO result = service.createCourseExam(1L, input);

        assertThat(result.title()).isEqualTo("Đề thi cuối khóa");
        ArgumentCaptor<Exam> captor = ArgumentCaptor.forClass(Exam.class);
        verify(exams).saveAndFlush(captor.capture());
        Exam saved = captor.getValue();
        assertThat(saved.getTitle()).isEqualTo("Đề thi cuối khóa");
        assertThat(saved.getAssessmentType()).isEqualTo("EXAM");
        assertThat(saved.getCourse()).isSameAs(course);
        assertThat(saved.getDurationMinutes()).isEqualTo(90);
    }

    @Test
    void createChapterExam_Success_Normal() {
        doReturn(chapter).when(service).ownedChapter(1L, 20L);
        CreateExamInput input = new CreateExamInput("Kiểm tra chương 1", 30, BigDecimal.valueOf(60.0), 1, false, null);
        ExamDetailDTO detail = new ExamDetailDTO(
            101L,
            "Kiểm tra chương 1",
            "CHAPTER_QUIZ",
            30,
            BigDecimal.valueOf(60.0),
            1,
            false,
            null,
            1L,
            20L,
            null,
            List.of(),
            null
        );
        when(examMapper.toDetailDto(any(Exam.class))).thenReturn(detail);

        ExamDetailDTO result = service.createChapterExam(1L, 20L, input);

        assertThat(result.assessmentType()).isEqualTo("CHAPTER_QUIZ");
        ArgumentCaptor<Exam> captor = ArgumentCaptor.forClass(Exam.class);
        verify(exams).saveAndFlush(captor.capture());
        Exam saved = captor.getValue();
        assertThat(saved.getAssessmentType()).isEqualTo("CHAPTER_QUIZ");
        assertThat(saved.getChapter()).isSameAs(chapter);
        assertThat(saved.getCourse()).isSameAs(course);
    }

    @Test
    void createLessonQuiz_Normal_Quiz() {
        doReturn(lesson).when(service).ownedLesson(1L, 300L);
        CreateExamInput input = new CreateExamInput("Trắc nghiệm bài 1", 15, BigDecimal.valueOf(50.0), 1, false, null);
        ExamDetailDTO detail = new ExamDetailDTO(
            102L,
            "Trắc nghiệm bài 1",
            "LESSON_QUIZ",
            15,
            BigDecimal.valueOf(50.0),
            1,
            false,
            null,
            1L,
            20L,
            300L,
            List.of(),
            null
        );
        when(examMapper.toDetailDto(any(Exam.class))).thenReturn(detail);

        ExamDetailDTO result = service.createLessonQuiz(1L, 300L, input, false);

        assertThat(result.assessmentType()).isEqualTo("LESSON_QUIZ");
        ArgumentCaptor<Exam> captor = ArgumentCaptor.forClass(Exam.class);
        verify(exams).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getAssessmentType()).isEqualTo("LESSON_QUIZ");
        assertThat(captor.getValue().getLesson()).isSameAs(lesson);
    }

    @Test
    void createLessonQuiz_Normal_Practice() {
        doReturn(lesson).when(service).ownedLesson(1L, 300L);
        CreateExamInput input = new CreateExamInput("Bài tập ôn luyện", 0, BigDecimal.valueOf(0.0), 99, false, null);
        ExamDetailDTO detail = new ExamDetailDTO(
            103L,
            "Bài tập ôn luyện",
            "PRACTICE",
            0,
            BigDecimal.valueOf(0.0),
            99,
            false,
            null,
            1L,
            20L,
            300L,
            List.of(),
            null
        );
        when(examMapper.toDetailDto(any(Exam.class))).thenReturn(detail);

        ExamDetailDTO result = service.createLessonQuiz(1L, 300L, input, true);

        assertThat(result.assessmentType()).isEqualTo("PRACTICE");
        ArgumentCaptor<Exam> captor = ArgumentCaptor.forClass(Exam.class);
        verify(exams).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getAssessmentType()).isEqualTo("PRACTICE");
    }

    @Test
    void createLessonCuePoint_Success_Normal() {
        doReturn(lesson).when(service).ownedLesson(1L, 300L);
        BigDecimal triggerSeconds = BigDecimal.valueOf(45.5);
        CreateExamInput input = new CreateExamInput("Câu hỏi tương tác 45s", 0, BigDecimal.valueOf(100.0), 1, false, triggerSeconds);
        when(
            exams.existsByLessonIdAndAssessmentTypeAndVideoTriggerSecondsAndIsDeletedFalse(300L, "VIDEO_CHECKPOINT", triggerSeconds)
        ).thenReturn(false);
        ExamDetailDTO detail = new ExamDetailDTO(
            104L,
            "Câu hỏi tương tác 45s",
            "VIDEO_CHECKPOINT",
            0,
            BigDecimal.valueOf(100.0),
            1,
            false,
            triggerSeconds,
            1L,
            20L,
            300L,
            List.of(),
            null
        );
        when(examMapper.toDetailDto(any(Exam.class))).thenReturn(detail);

        ExamDetailDTO result = service.createLessonCuePoint(1L, 300L, input);

        assertThat(result.assessmentType()).isEqualTo("VIDEO_CHECKPOINT");
        assertThat(result.videoTriggerSeconds()).isEqualTo(triggerSeconds);
        ArgumentCaptor<Exam> captor = ArgumentCaptor.forClass(Exam.class);
        verify(exams).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getVideoTriggerSeconds()).isEqualTo(triggerSeconds);
    }

    @Test
    void createLessonCuePoint_MissingOrNegativeSeconds_Abnormal() {
        doReturn(lesson).when(service).ownedLesson(1L, 300L);
        CreateExamInput inputNull = new CreateExamInput("Cue null", 0, BigDecimal.valueOf(100.0), 1, false, null);
        assertThatThrownBy(() -> service.createLessonCuePoint(1L, 300L, inputNull))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("videoTriggerSeconds is required");

        CreateExamInput inputNeg = new CreateExamInput("Cue neg", 0, BigDecimal.valueOf(100.0), 1, false, BigDecimal.valueOf(-5));
        assertThatThrownBy(() -> service.createLessonCuePoint(1L, 300L, inputNeg))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("videoTriggerSeconds is required");
    }

    @Test
    void createLessonCuePoint_DuplicateTrigger_Abnormal() {
        doReturn(lesson).when(service).ownedLesson(1L, 300L);
        BigDecimal triggerSeconds = BigDecimal.valueOf(30.0);
        CreateExamInput input = new CreateExamInput("Cue duplicate", 0, BigDecimal.valueOf(100.0), 1, false, triggerSeconds);
        when(
            exams.existsByLessonIdAndAssessmentTypeAndVideoTriggerSecondsAndIsDeletedFalse(300L, "VIDEO_CHECKPOINT", triggerSeconds)
        ).thenReturn(true);

        assertThatThrownBy(() -> service.createLessonCuePoint(1L, 300L, input))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("đã có câu hỏi tương tác");
    }

    @Test
    void updateExam_Success_Normal() {
        Exam existing = new Exam().id(50L).title("Old Title").version(1L);
        doReturn(existing).when(service).ownedExam(1L, 50L);
        UpdateExamInput input = new UpdateExamInput("Updated Title", 45, BigDecimal.valueOf(75.0), 3, true, null);
        ExamDetailDTO detail = new ExamDetailDTO(
            50L,
            "Updated Title",
            "EXAM",
            45,
            BigDecimal.valueOf(75.0),
            3,
            true,
            null,
            1L,
            null,
            null,
            List.of(),
            null
        );
        when(examMapper.toDetailDto(existing)).thenReturn(detail);

        ExamDetailDTO result = service.updateExam(1L, 50L, input);

        assertThat(existing.getTitle()).isEqualTo("Updated Title");
        assertThat(existing.getDurationMinutes()).isEqualTo(45);
        assertThat(existing.getPassScore()).isEqualTo(BigDecimal.valueOf(75.0));
        assertThat(existing.getMaxAttempts()).isEqualTo(3);
        assertThat(existing.getShuffleQuestions()).isTrue();
        assertThat(existing.getVersion()).isEqualTo(2L);
        verify(exams).saveAndFlush(existing);
    }

    @Test
    void deleteExam_Success_Normal() {
        Exam existing = new Exam().id(50L).version(1L);
        existing.setIsDeleted(false);
        doReturn(existing).when(service).ownedExam(1L, 50L);

        service.deleteExam(1L, 50L);

        assertThat(existing.getIsDeleted()).isTrue();
        assertThat(existing.getStatus()).isEqualTo(RecordStatus.ARCHIVED);
        assertThat(existing.getVersion()).isEqualTo(2L);
        verify(exams).saveAndFlush(existing);
    }

    @Test
    void addQuestionToExam_Success_Normal() {
        Exam exam = new Exam().id(50L);
        doReturn(exam).when(service).ownedExam(1L, 50L);

        List<OptionInput> options = List.of(new OptionInput(null, "Đáp án A", true, 0), new OptionInput(null, "Đáp án B", false, 1));
        QuestionInput input = new QuestionInput(null, "Câu hỏi 1?", "MCQ", "EASY", "Giải thích", BigDecimal.valueOf(2.0), 0, options);

        when(examMapper.toDetailDto(exam)).thenReturn(
            new ExamDetailDTO(50L, "Exam", "EXAM", 30, null, 1, false, null, 1L, null, null, List.of(), null)
        );

        service.addQuestionToExam(1L, 50L, input);

        ArgumentCaptor<Question> qCaptor = ArgumentCaptor.forClass(Question.class);
        verify(questions).saveAndFlush(qCaptor.capture());
        assertThat(qCaptor.getValue().getContent()).isEqualTo("Câu hỏi 1?");
        assertThat(qCaptor.getValue().getDifficulty()).isEqualTo(1); // "EASY" -> 1

        verify(questionOptions, times(2)).saveAndFlush(any(QuestionOption.class));

        ArgumentCaptor<ExamQuestion> eqCaptor = ArgumentCaptor.forClass(ExamQuestion.class);
        verify(examQuestions).saveAndFlush(eqCaptor.capture());
        assertThat(eqCaptor.getValue().getScore()).isEqualTo(BigDecimal.valueOf(2.0));
        assertThat(eqCaptor.getValue().getOrderIndex()).isEqualTo(0);
    }

    @Test
    void addQuestionToExam_EmptyOptions_Abnormal() {
        Exam exam = new Exam().id(50L);
        doReturn(exam).when(service).ownedExam(1L, 50L);
        QuestionInput input = new QuestionInput(null, "Không có đáp án?", "MCQ", 3, null, BigDecimal.ONE, 0, List.of());

        assertThatThrownBy(() -> service.addQuestionToExam(1L, 50L, input))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("At least one option is required");
    }

    @Test
    void addQuestionToExam_NoCorrectOption_Abnormal() {
        Exam exam = new Exam().id(50L);
        doReturn(exam).when(service).ownedExam(1L, 50L);
        List<OptionInput> options = List.of(new OptionInput(null, "A", false, 0), new OptionInput(null, "B", false, 1));
        QuestionInput input = new QuestionInput(null, "Không có đáp án đúng?", "MCQ", 3, null, BigDecimal.ONE, 0, options);

        assertThatThrownBy(() -> service.addQuestionToExam(1L, 50L, input))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("At least one option must be marked as correct");
    }

    @Test
    void deleteQuestionFromExam_Success_Normal() {
        Exam exam = new Exam().id(50L);
        doReturn(exam).when(service).ownedExam(1L, 50L);
        when(examMapper.toDetailDto(exam)).thenReturn(
            new ExamDetailDTO(50L, "Exam", "EXAM", 30, null, 1, false, null, 1L, null, null, List.of(), null)
        );

        service.deleteQuestionFromExam(1L, 50L, 201L);

        verify(examQuestions).deleteByExamIdAndQuestionId(50L, 201L);
    }
}
