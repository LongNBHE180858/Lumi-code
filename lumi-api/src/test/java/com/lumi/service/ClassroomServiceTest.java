package com.lumi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.lumi.domain.Assignment;
import com.lumi.domain.Authority;
import com.lumi.domain.ClassMember;
import com.lumi.domain.StudyClass;
import com.lumi.domain.User;
import com.lumi.domain.enumeration.ClassMemberStatus;
import com.lumi.repository.AssignmentRepository;
import com.lumi.repository.AssignmentStudentRepository;
import com.lumi.repository.ClassMaterialRepository;
import com.lumi.repository.ClassMemberRepository;
import com.lumi.repository.StudyClassRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.service.dto.studyClass.ClassroomDTO;
import com.lumi.service.storage.ClassroomObjectStorage;
import com.lumi.service.storage.ClassroomStorageCleanup;
import com.lumi.service.storage.CourseFileValidator;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ClassroomServiceTest {

    @Mock
    private StudyClassRepository classes;

    @Mock
    private ClassMemberRepository members;

    @Mock
    private ClassMaterialRepository materials;

    @Mock
    private AssignmentRepository assignments;

    @Mock
    private AssignmentStudentRepository recipients;

    @Mock
    private UserRepository users;

    @Mock
    private ClassroomObjectStorage fileStorage;

    @Mock
    private ClassroomStorageCleanup fileCleanup;

    private ClassroomService service;
    private User instructor;

    @BeforeEach
    void setUp() {
        service = new ClassroomService(
            classes,
            members,
            materials,
            assignments,
            recipients,
            users,
            new CourseFileValidator(),
            fileStorage,
            fileCleanup
        );
        instructor = new User();
        instructor.setId(10L);
        instructor.setLogin("teacher");
        instructor.setActivated(true);
        instructor.setAuthorities(Set.of(new Authority().name(AuthoritiesConstants.INSTRUCTOR)));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("teacher", "unused"));
        when(users.findOneByLogin("teacher")).thenReturn(Optional.of(instructor));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void removesActiveStudentWithoutDeletingMembership() {
        StudyClass studyClass = new StudyClass().id(1L).instructor(instructor).isDeleted(false);
        ClassMember member = new ClassMember().id(2L).studyClass(studyClass).status(ClassMemberStatus.ACTIVE.name());
        when(classes.findById(1L)).thenReturn(Optional.of(studyClass));
        when(members.findById(2L)).thenReturn(Optional.of(member));

        service.removeMember(1L, 2L, false);

        assertThat(member.getStatus()).isEqualTo(ClassMemberStatus.REMOVED.name());
        assertThat(member.getLeftAt()).isNotNull();
        verify(members).save(member);
        verify(members, never()).delete(any(ClassMember.class));
    }

    @Test
    void refusesChangesToAnotherInstructorsClass() {
        User otherInstructor = new User();
        otherInstructor.setId(11L);
        StudyClass studyClass = new StudyClass().id(1L).instructor(otherInstructor).isDeleted(false);
        when(classes.findById(1L)).thenReturn(Optional.of(studyClass));

        assertThatThrownBy(() -> service.removeMember(1L, 2L, false)).isInstanceOf(ResponseStatusException.class);
        verify(members, never()).save(any());
    }

    @Test
    void storesAssignmentPdfInClassroomNamespace() {
        StudyClass studyClass = new StudyClass().id(1L).instructor(instructor).isDeleted(false);
        when(classes.findById(1L)).thenReturn(Optional.of(studyClass));
        byte[] bytes = "%PDF-1.7\nwork\n%%EOF".getBytes(StandardCharsets.US_ASCII);
        var file = new MockMultipartFile("file", "work.pdf", "application/pdf", bytes);
        when(
            fileStorage.upload(eq(1L), eq(CourseFileValidator.Kind.DOCUMENT), any(Path.class), eq("work.pdf"), eq("application/pdf"))
        ).thenReturn(
            new ClassroomObjectStorage.Uploaded(
                "https://r2.example/bucket/classrooms/1/document/work.pdf",
                "work.pdf",
                "application/pdf",
                bytes.length
            )
        );
        when(assignments.save(any(Assignment.class))).thenAnswer(invocation -> {
            Assignment item = invocation.getArgument(0);
            item.setId(9L);
            return item;
        });

        var result = service.createTask(1L, new ClassroomDTO.CreateTaskRequest("Bài tập", "Đọc PDF", null), file);

        assertThat(result.fileName()).isEqualTo("work.pdf");
        assertThat(result.fileType()).isEqualTo("PDF");
        verify(fileCleanup).onRollback(eq(1L), contains("classrooms/1/document/"));
    }
}
