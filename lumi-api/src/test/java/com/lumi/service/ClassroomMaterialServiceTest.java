package com.lumi.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.lumi.domain.ClassMaterial;
import com.lumi.domain.StudyClass;
import com.lumi.domain.User;
import com.lumi.repository.ClassMaterialRepository;
import com.lumi.repository.ClassMemberRepository;
import com.lumi.repository.StudyClassRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.storage.CourseFileValidator;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ClassroomMaterialServiceTest {

    @Mock
    StudyClassRepository classes;

    @Mock
    ClassMemberRepository members;

    @Mock
    ClassMaterialRepository materials;

    @Mock
    UserRepository users;

    @TempDir
    Path uploadRoot;

    ClassroomMaterialService service;
    User teacher;
    StudyClass studyClass;

    @BeforeEach
    void setUp() {
        teacher = new User();
        teacher.setId(10L);
        teacher.setLogin("teacher");
        studyClass = new StudyClass().id(20L).instructor(teacher).isDeleted(false);
        service = new ClassroomMaterialService(classes, members, materials, users, new CourseFileValidator(), uploadRoot.toString());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("teacher", "unused"));
        lenient().when(users.findOneByLogin("teacher")).thenReturn(Optional.of(teacher));
        lenient().when(classes.findById(20L)).thenReturn(Optional.of(studyClass));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void uploadsPdfAndServesItOnlyThroughOwnedClass() throws Exception {
        byte[] content = "%PDF-1.7\nlesson\n%%EOF".getBytes(StandardCharsets.US_ASCII);
        var file = new MockMultipartFile("file", "lesson.pdf", "application/pdf", content);
        when(materials.findByStudyClassIdOrderByOrderIndexAscIdAsc(20L)).thenReturn(List.of());
        when(materials.saveAndFlush(any(ClassMaterial.class))).thenAnswer(invocation -> {
            ClassMaterial item = invocation.getArgument(0);
            item.setId(30L);
            return item;
        });
        when(materials.save(any(ClassMaterial.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var uploaded = service.upload(20L, "Bài 1", file);

        assertThat(uploaded.fileType()).isEqualTo("PDF");
        assertThat(uploaded.fileUrl()).isEqualTo("/api/classes/20/materials/30/file");
        assertThat(uploaded.orderIndex()).isZero();
        var saved = org.mockito.ArgumentCaptor.forClass(ClassMaterial.class);
        verify(materials).save(saved.capture());
        when(materials.findById(30L)).thenReturn(Optional.of(saved.getValue()));
        assertThat(service.access(20L, 30L).resource().getContentAsByteArray()).isEqualTo(content);
    }

    @Test
    void rejectsReorderWithMissingOrForeignMaterial() {
        ClassMaterial first = new ClassMaterial().id(1L).studyClass(studyClass).orderIndex(0);
        ClassMaterial second = new ClassMaterial().id(2L).studyClass(studyClass).orderIndex(1);
        when(materials.findByStudyClassIdOrderByOrderIndexAscIdAsc(20L)).thenReturn(List.of(first, second));

        assertThatThrownBy(() -> service.reorder(20L, List.of(1L, 999L))).isInstanceOf(ResponseStatusException.class);
        verify(materials, never()).saveAll(any());
    }

    @Test
    void refusesFileToStudentOutsideTheClass() {
        User student = new User();
        student.setId(11L);
        student.setLogin("student");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("student", "unused"));
        when(users.findOneByLogin("student")).thenReturn(Optional.of(student));
        ClassMaterial material = new ClassMaterial().id(30L).studyClass(studyClass).status("PUBLISHED");
        when(materials.findById(30L)).thenReturn(Optional.of(material));

        assertThatThrownBy(() -> service.access(20L, 30L)).isInstanceOf(ResponseStatusException.class);
    }
}
