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
import com.lumi.service.storage.ClassroomObjectStorage;
import com.lumi.service.storage.ClassroomStorageCleanup;
import com.lumi.service.storage.CourseFileValidator;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

    @Mock
    ClassroomObjectStorage storage;

    @Mock
    ClassroomStorageCleanup cleanup;

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
        service = new ClassroomMaterialService(
            classes,
            members,
            materials,
            users,
            new CourseFileValidator(),
            storage,
            cleanup,
            uploadRoot.toString()
        );
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("teacher", "unused"));
        lenient().when(users.findOneByLogin("teacher")).thenReturn(Optional.of(teacher));
        lenient().when(classes.findById(20L)).thenReturn(Optional.of(studyClass));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void uploadsPdfToClassroomStorageAndSignsAccess() throws Exception {
        byte[] content = "%PDF-1.7\nlesson\n%%EOF".getBytes(StandardCharsets.US_ASCII);
        var file = new MockMultipartFile("file", "lesson.pdf", "application/pdf", content);
        when(materials.findByStudyClassIdOrderByOrderIndexAscIdAsc(20L)).thenReturn(List.of());
        when(materials.saveAndFlush(any(ClassMaterial.class))).thenAnswer(invocation -> {
            ClassMaterial item = invocation.getArgument(0);
            item.setId(30L);
            return item;
        });
        when(
            storage.upload(eq(20L), eq(CourseFileValidator.Kind.DOCUMENT), any(Path.class), eq("lesson.pdf"), eq("application/pdf"))
        ).thenReturn(
            new ClassroomObjectStorage.Uploaded(
                "https://r2.example/bucket/classrooms/20/document/file.pdf",
                "lesson.pdf",
                "application/pdf",
                content.length
            )
        );

        var uploaded = service.upload(20L, "Bài 1", file);

        assertThat(uploaded.fileType()).isEqualTo("PDF");
        assertThat(uploaded.fileUrl()).isEqualTo("/api/classes/20/materials/30/file");
        assertThat(uploaded.orderIndex()).isZero();
        var saved = org.mockito.ArgumentCaptor.forClass(ClassMaterial.class);
        verify(materials).saveAndFlush(saved.capture());
        assertThat(saved.getValue().getFileUrl()).contains("classrooms/20/document/");
        verify(cleanup).onRollback(eq(20L), eq(saved.getValue().getFileUrl()));
        when(materials.findById(30L)).thenReturn(Optional.of(saved.getValue()));
        when(storage.access(eq(20L), eq(saved.getValue().getFileUrl()), anyString())).thenReturn(
            new ClassroomObjectStorage.Access("https://signed.example/pdf", java.time.Instant.now().plusSeconds(300))
        );
        assertThat(service.accessUrl(20L, 30L).url()).isEqualTo("https://signed.example/pdf");
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

    @Test
    void keepsPreviouslyUploadedLocalPdfReadable() throws Exception {
        String key = "11111111-1111-1111-1111-111111111111.pdf";
        byte[] content = "%PDF-1.7\nold\n%%EOF".getBytes(StandardCharsets.US_ASCII);
        Files.write(uploadRoot.resolve(key), content);
        ClassMaterial material = new ClassMaterial()
            .id(30L)
            .studyClass(studyClass)
            .status("PUBLISHED")
            .fileType("PDF")
            .fileObjectKey(key)
            .fileUrl("/api/classes/20/materials/30/file");
        when(materials.findById(30L)).thenReturn(Optional.of(material));

        assertThat(service.access(20L, 30L).resource().getContentAsByteArray()).isEqualTo(content);
        assertThat(service.accessUrl(20L, 30L).url()).isEqualTo("/api/classes/20/materials/30/file");
    }
}
