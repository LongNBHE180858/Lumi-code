package com.lumi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.lumi.domain.Assignment;
import com.lumi.domain.AssignmentSubmission;
import com.lumi.domain.Authority;
import com.lumi.domain.ClassMember;
import com.lumi.domain.StudyClass;
import com.lumi.domain.User;
import com.lumi.repository.AssignmentRepository;
import com.lumi.repository.AssignmentStudentRepository;
import com.lumi.repository.AssignmentSubmissionRepository;
import com.lumi.repository.ClassMemberRepository;
import com.lumi.repository.StudyClassRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.service.dto.studyClass.ClassAssignmentDTO;
import com.lumi.service.storage.ClassroomObjectStorage;
import com.lumi.service.storage.ClassroomStorageCleanup;
import com.lumi.service.storage.CourseFileValidator;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ClassAssignmentServiceTest {

    @Mock
    AssignmentRepository assignments;

    @Mock
    AssignmentStudentRepository recipients;

    @Mock
    AssignmentSubmissionRepository submissions;

    @Mock
    ClassMemberRepository members;

    @Mock
    StudyClassRepository classes;

    @Mock
    UserRepository users;

    @Mock
    ClassroomObjectStorage fileStorage;

    @Mock
    ClassroomStorageCleanup storageCleanup;

    ClassAssignmentService service;
    User user;
    StudyClass studyClass;
    Assignment assignment;

    @BeforeEach
    void setUp() {
        service = new ClassAssignmentService(
            assignments,
            recipients,
            submissions,
            members,
            classes,
            users,
            fileStorage,
            storageCleanup,
            new CourseFileValidator()
        );
        user = new User();
        user.setId(7L);
        user.setLogin("student");
        user.setActivated(true);
        user.setAuthorities(Set.of(new Authority().name(AuthoritiesConstants.STUDENT)));
        studyClass = new StudyClass().id(3L).isDeleted(false);
        assignment = new Assignment().id(9L).studyClass(studyClass).status("PUBLISHED").isDeleted(false);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("student", "unused"));
        when(users.findOneByLogin("student")).thenReturn(Optional.of(user));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void assigned() {
        when(assignments.findByIdForUpdate(9L)).thenReturn(Optional.of(assignment));
        when(classes.findById(3L)).thenReturn(Optional.of(studyClass));
        when(members.findByStudyClassIdAndStudentId(3L, 7L)).thenReturn(
            Optional.of(new ClassMember().studyClass(studyClass).student(user).status("ACTIVE"))
        );
        when(recipients.existsByAssignmentIdAndStudentId(9L, 7L)).thenReturn(true);
    }

    @Test
    void deniesAttachmentToStudentNotAssignedToTask() {
        User teacher = new User();
        teacher.setId(8L);
        studyClass.setInstructor(teacher);
        assignment.setFileUrl("https://r2.example/bucket/classrooms/3/document/work.pdf");
        when(assignments.findById(9L)).thenReturn(Optional.of(assignment));
        when(classes.findById(3L)).thenReturn(Optional.of(studyClass));
        when(members.findByStudyClassIdAndStudentId(3L, 7L)).thenReturn(
            Optional.of(new ClassMember().studyClass(studyClass).student(user).status("ACTIVE"))
        );

        assertThatThrownBy(() -> service.fileAccess(3L, 9L)).isInstanceOf(ResponseStatusException.class);
        verifyNoInteractions(fileStorage);
    }

    @Test
    void submitsOnlyOnceForAssignedStudent() {
        assigned();
        when(submissions.findByAssignmentIdAndStudentIdOrderByIdDesc(9L, 7L)).thenReturn(List.of());
        when(submissions.save(any())).thenAnswer(invocation -> {
            AssignmentSubmission item = invocation.getArgument(0);
            item.setId(12L);
            return item;
        });

        var result = service.submit(3L, 9L, new ClassAssignmentDTO.Submit("  Lời giải  "));

        assertThat(result.content()).isEqualTo("Lời giải");
        assertThat(result.status()).isEqualTo("SUBMITTED");
        assertThat(result.version()).isZero();
        verify(submissions).save(any());
    }

    @Test
    void submitsPdfWithoutTextUsingExistingAttachmentsColumn() {
        assigned();
        when(submissions.findByAssignmentIdAndStudentIdOrderByIdDesc(9L, 7L)).thenReturn(List.of());
        when(fileStorage.upload(eq(3L), eq(CourseFileValidator.Kind.DOCUMENT), any(), eq("answer.pdf"), eq("application/pdf"))).thenReturn(
            new ClassroomObjectStorage.Uploaded("https://r2.example/classrooms/3/document/file.pdf", "answer.pdf", "application/pdf", 20)
        );
        when(submissions.save(any())).thenAnswer(invocation -> {
            AssignmentSubmission item = invocation.getArgument(0);
            item.setId(12L);
            return item;
        });
        var file = new MockMultipartFile("file", "answer.pdf", "application/pdf", "%PDF-1.4\n%%EOF".getBytes(StandardCharsets.US_ASCII));

        var result = service.submitWithFile(3L, 9L, "", file);

        assertThat(result.content()).isEmpty();
        assertThat(result.attachments()).hasSize(1);
        assertThat(result.attachments().getFirst().fileName()).isEqualTo("answer.pdf");
        assertThat(result.toString()).doesNotContain("https://r2.example");
        verify(storageCleanup).onRollback(eq(3L), any());
    }

    @Test
    void preventsStudentFromOpeningAnotherStudentsSubmissionFile() {
        User otherStudent = new User();
        otherStudent.setId(8L);
        User teacher = new User();
        teacher.setId(10L);
        studyClass.setInstructor(teacher);
        when(assignments.findById(9L)).thenReturn(Optional.of(assignment));
        when(submissions.findById(12L)).thenReturn(
            Optional.of(new AssignmentSubmission().id(12L).assignment(assignment).student(otherStudent).attachments("[]"))
        );

        assertThatThrownBy(() -> service.submissionFileAccess(3L, 9L, 12L)).isInstanceOfSatisfying(ResponseStatusException.class, error ->
            assertThat(error.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
        );
        verifyNoInteractions(fileStorage);
    }

    @Test
    void letsStudentOpenOnlyTheirOwnSubmittedFile() {
        User teacher = new User();
        teacher.setId(10L);
        studyClass.setInstructor(teacher);
        when(assignments.findById(9L)).thenReturn(Optional.of(assignment));
        when(submissions.findById(12L)).thenReturn(
            Optional.of(
                new AssignmentSubmission()
                    .id(12L)
                    .assignment(assignment)
                    .student(user)
                    .attachments(
                        "[{\"url\":\"https://r2.example/classrooms/3/document/file.pdf\",\"fileName\":\"answer.pdf\",\"fileType\":\"PDF\",\"sizeBytes\":20}]"
                    )
            )
        );
        when(classes.findById(3L)).thenReturn(Optional.of(studyClass));
        when(members.findByStudyClassIdAndStudentId(3L, 7L)).thenReturn(
            Optional.of(new ClassMember().studyClass(studyClass).student(user).status("ACTIVE"))
        );
        when(recipients.existsByAssignmentIdAndStudentId(9L, 7L)).thenReturn(true);
        when(fileStorage.access(3L, "https://r2.example/classrooms/3/document/file.pdf", "answer.pdf")).thenReturn(
            new ClassroomObjectStorage.Access("https://signed.example/answer", Instant.now().plusSeconds(300))
        );

        var access = service.submissionFileAccess(3L, 9L, 12L);

        assertThat(access.url()).isEqualTo("https://signed.example/answer");
    }

    @Test
    void rejectsLateSubmission() {
        assignment.setDueAt(Instant.now().minusSeconds(60));
        assigned();

        assertThatThrownBy(() -> service.submit(3L, 9L, new ClassAssignmentDTO.Submit("Lời giải"))).isInstanceOfSatisfying(
            ResponseStatusException.class,
            e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT)
        );
        verify(submissions, never()).save(any());
    }

    @Test
    void rejectsSubmissionNotAssignedToStudent() {
        when(assignments.findByIdForUpdate(9L)).thenReturn(Optional.of(assignment));
        when(classes.findById(3L)).thenReturn(Optional.of(studyClass));
        when(members.findByStudyClassIdAndStudentId(3L, 7L)).thenReturn(
            Optional.of(new ClassMember().studyClass(studyClass).student(user).status("ACTIVE"))
        );

        assertThatThrownBy(() -> service.submit(3L, 9L, new ClassAssignmentDTO.Submit("Lời giải"))).isInstanceOfSatisfying(
            ResponseStatusException.class,
            e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN)
        );
        verify(submissions, never()).save(any());
    }

    @Test
    void refusesStudentAccessToAnotherClass() {
        when(assignments.findById(9L)).thenReturn(Optional.of(assignment));
        assertThatThrownBy(() -> service.mine(4L, 9L)).isInstanceOfSatisfying(ResponseStatusException.class, e ->
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
        );
    }

    @Test
    void rejectsStaleGradeVersion() {
        user.setAuthorities(Set.of(new Authority().name(AuthoritiesConstants.INSTRUCTOR)));
        studyClass.setInstructor(user);
        User student = new User();
        student.setId(8L);
        AssignmentSubmission submission = new AssignmentSubmission().id(12L).assignment(assignment).student(student).rowVersion(2L);
        when(classes.findById(3L)).thenReturn(Optional.of(studyClass));
        when(assignments.findByIdForUpdate(9L)).thenReturn(Optional.of(assignment));
        when(submissions.findById(12L)).thenReturn(Optional.of(submission));
        when(recipients.existsByAssignmentIdAndStudentId(9L, 8L)).thenReturn(true);

        assertThatThrownBy(() -> service.grade(3L, 9L, 12L, new ClassAssignmentDTO.Grade(BigDecimal.TEN, "OK", 1L))).isInstanceOfSatisfying(
            ResponseStatusException.class,
            e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT)
        );
        verify(submissions, never()).save(any());
    }
}
