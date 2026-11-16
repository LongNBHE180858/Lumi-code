package com.lumi.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.lumi.domain.*;
import com.lumi.domain.enumeration.RecordStatus;
import com.lumi.repository.*;
import com.lumi.service.dto.studyClass.ClassExamDTO;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ClassroomExamServiceTest {

    @Mock
    StudyClassRepository classes;

    @Mock
    ClassMemberRepository members;

    @Mock
    ClassMaterialRepository materials;

    @Mock
    UserRepository users;

    @Mock
    ExamRepository exams;

    @Mock
    ExamQuestionRepository examQuestions;

    @Mock
    QuestionRepository questions;

    @Mock
    QuestionOptionRepository options;

    @Mock
    ExamAttemptRepository attempts;

    @Mock
    ExamAnswerRepository answers;

    @Mock
    QuestionBankRepository questionBanks;

    @Mock
    QuestionKnowledgeLinkRepository questionKnowledgeLinks;

    ClassroomExamService service;
    User teacher;
    User student;
    StudyClass studyClass;
    ClassMaterial lesson;
    Exam exam;

    @BeforeEach
    void setUp() {
        teacher = new User();
        teacher.setId(10L);
        teacher.setLogin("teacher");
        teacher.setActivated(true);
        student = new User();
        student.setId(11L);
        student.setLogin("student");
        student.setActivated(true);
        studyClass = new StudyClass().id(20L).instructor(teacher).isDeleted(false);
        lesson = new ClassMaterial().id(30L).studyClass(studyClass).status("PUBLISHED");
        exam = new Exam()
            .id(40L)
            .studyClass(studyClass)
            .classMaterial(lesson)
            .status(RecordStatus.ACTIVE)
            .title("Kiểm tra bài 1")
            .durationMinutes(15)
            .passScore(BigDecimal.valueOf(50))
            .maxAttempts(3)
            .isDeleted(false);
        service = new ClassroomExamService(
            classes,
            members,
            materials,
            users,
            exams,
            examQuestions,
            questions,
            options,
            attempts,
            answers,
            questionBanks,
            questionKnowledgeLinks
        );
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("teacher", "unused"));
        lenient().when(users.findOneByLogin("teacher")).thenReturn(Optional.of(teacher));
        lenient().when(users.findOneByLogin("student")).thenReturn(Optional.of(student));
        lenient().when(classes.findById(20L)).thenReturn(Optional.of(studyClass));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void cannotCreateExamForLessonInAnotherClass() {
        ClassMaterial foreign = new ClassMaterial().id(31L).studyClass(new StudyClass().id(99L));
        when(materials.findById(31L)).thenReturn(Optional.of(foreign));
        var input = new ClassExamDTO.SaveExam(
            31L,
            "Đề",
            15,
            BigDecimal.valueOf(50),
            null,
            List.of(
                new ClassExamDTO.QuestionInput(
                    "Câu hỏi",
                    "Giải thích",
                    BigDecimal.ONE,
                    List.of(new ClassExamDTO.OptionInput("A"), new ClassExamDTO.OptionInput("B")),
                    0
                )
            )
        );

        assertThatThrownBy(() -> service.createExam(20L, input)).isInstanceOf(ResponseStatusException.class);
        verify(exams, never()).save(any());
    }

    @Test
    void studentAttemptDoesNotExposeCorrectOption() {
        actAsStudent();
        when(exams.findByIdForUpdate(40L)).thenReturn(Optional.of(exam));
        when(attempts.findByExamIdAndStudentIdOrderByStartTimeDesc(40L, 11L)).thenReturn(List.of());
        when(attempts.save(any(ExamAttempt.class))).thenAnswer(invocation -> {
            ExamAttempt attempt = invocation.getArgument(0);
            attempt.setId(50L);
            return attempt;
        });
        Question question = new Question().id(60L).content("2 + 2 = ?");
        when(examQuestions.findByExamIdOrderByOrderIndexAsc(40L)).thenReturn(
            List.of(new ExamQuestion().question(question).score(BigDecimal.ONE).orderIndex(0))
        );
        when(options.findByQuestionIdOrderByOrderIndexAsc(60L)).thenReturn(
            List.of(new QuestionOption().id(70L).content("4").isCorrect(true), new QuestionOption().id(71L).content("5").isCorrect(false))
        );

        var attempt = service.startAttempt(20L, 40L);

        assertThat(attempt.attemptId()).isEqualTo(50L);
        assertThat(attempt.questions()).hasSize(1);
        assertThat(attempt.questions().getFirst().options()).extracting(ClassExamDTO.Option::content).containsExactly("4", "5");
        assertThat(attempt.questions().getFirst().options().getFirst().toString()).doesNotContain("correct");
    }

    @Test
    void gradesSubmissionAndRecordsPerQuestionFeedback() {
        actAsStudent();
        when(exams.findById(40L)).thenReturn(Optional.of(exam));
        ExamAttempt attempt = new ExamAttempt()
            .id(50L)
            .exam(exam)
            .student(student)
            .status("IN_PROGRESS")
            .deadlineAt(Instant.now().plusSeconds(900));
        when(attempts.findByIdForUpdate(50L)).thenReturn(Optional.of(attempt));
        Question question = new Question().id(60L).content("2 + 2 = ?").explanation("Hai cộng hai bằng bốn");
        when(examQuestions.findByExamIdOrderByOrderIndexAsc(40L)).thenReturn(
            List.of(new ExamQuestion().question(question).score(BigDecimal.valueOf(2)).orderIndex(0))
        );
        QuestionOption correct = new QuestionOption().id(70L).question(question).content("4").isCorrect(true);
        QuestionOption wrong = new QuestionOption().id(71L).question(question).content("5").isCorrect(false);
        when(options.findByQuestionIdOrderByOrderIndexAsc(60L)).thenReturn(List.of(correct, wrong));
        List<ExamAnswer> saved = new ArrayList<>();
        when(answers.save(any(ExamAnswer.class))).thenAnswer(invocation -> {
            ExamAnswer answer = invocation.getArgument(0);
            saved.add(answer);
            return answer;
        });
        when(answers.findByAttemptId(50L)).thenAnswer(invocation -> saved);

        var result = service.submit(20L, 40L, 50L, new ClassExamDTO.Submit(List.of(new ClassExamDTO.AnswerInput(60L, 71L))));

        assertThat(result.score()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.feedback()).hasSize(1);
        assertThat(result.feedback().getFirst().correct()).isEqualTo("4");
        assertThat(result.feedback().getFirst().isCorrect()).isFalse();
        assertThat(saved.getFirst().getIsCorrect()).isFalse();
        assertThat(attempt.getStatus()).isEqualTo("SUBMITTED");
    }

    @Test
    void cannotReplaceQuestionsAfterStudentStartedExam() {
        when(exams.findById(40L)).thenReturn(Optional.of(exam));
        when(attempts.existsByExamId(40L)).thenReturn(true);
        var replacement = new ClassExamDTO.QuestionInput(
            "Câu mới",
            "",
            BigDecimal.ONE,
            List.of(new ClassExamDTO.OptionInput("A"), new ClassExamDTO.OptionInput("B")),
            0
        );
        var input = new ClassExamDTO.UpdateExam("Đề", 15, BigDecimal.valueOf(50), null, "ACTIVE", List.of(replacement));

        assertThatThrownBy(() -> service.updateExam(20L, 40L, input)).isInstanceOf(ResponseStatusException.class);
        verify(examQuestions, never()).deleteAll(any());
    }

    private void actAsStudent() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("student", "unused"));
        when(members.findByStudyClassIdAndStudentId(20L, 11L)).thenReturn(
            Optional.of(new ClassMember().studyClass(studyClass).student(student).status("ACTIVE"))
        );
    }
}
