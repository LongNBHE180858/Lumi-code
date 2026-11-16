package com.lumi.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.lumi.domain.*;
import com.lumi.repository.CourseMaterialRepository;
import com.lumi.service.dto.InstructorCourseDTOs.Builder;
import com.lumi.service.impl.InstructorCourseMediaService;
import com.lumi.service.impl.InstructorCourseService;
import com.lumi.service.storage.*;
import com.lumi.service.storage.CourseFileValidator.Kind;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class InstructorCourseMediaServiceTest {

    @Mock
    InstructorCourseService courses;

    @Mock
    CourseMaterialRepository materials;

    @Mock
    CourseFileValidator validator;

    @Mock
    CourseObjectStorage storage;

    @Mock
    CourseStorageCleanup cleanup;

    @Mock
    EntityManager em;

    @InjectMocks
    InstructorCourseMediaService service;

    @TempDir
    Path tempDir;

    private Course course;
    private Lesson lesson;
    private final Builder mockBuilder = new Builder(null, List.of(), 0, 0, 0);

    @BeforeEach
    void setup() {
        course = new Course().id(1L);
        lesson = new Lesson().id(101L).chapter(new Chapter().id(10L).course(course));
    }

    @Test
    void courseMedia_UploadThumbnail_Normal() throws Exception {
        Path tempFile = Files.createFile(tempDir.resolve("thumb.png"));
        MockMultipartFile file = new MockMultipartFile("file", "thumb.png", "image/png", new byte[10]);
        CourseFileValidator.ValidatedFile validated = new CourseFileValidator.ValidatedFile(tempFile, "thumb.png", "image/png");

        when(courses.editable(1L, 1L)).thenReturn(course);
        when(validator.validate(file, Kind.THUMBNAIL)).thenReturn(validated);
        when(storage.upload(1L, Kind.THUMBNAIL, tempFile, "thumb.png", "image/png")).thenReturn(
            new CourseObjectStorage.Uploaded("https://r2/thumb.png", "thumb.png", "image/png", 10L)
        );
        when(courses.changed(course)).thenReturn(mockBuilder);

        Builder result = service.courseMedia(1L, 1L, file, true);

        assertThat(course.getThumbnailUrl()).isEqualTo("https://r2/thumb.png");
        verify(cleanup).onRollback("https://r2/thumb.png");
        verify(courses).changed(course);
        assertThat(result).isSameAs(mockBuilder);
    }

    @Test
    void courseMedia_UploadIntroVideo_Normal() throws Exception {
        Path tempFile = Files.createFile(tempDir.resolve("intro.mp4"));
        MockMultipartFile file = new MockMultipartFile("file", "intro.mp4", "video/mp4", new byte[100]);
        CourseFileValidator.ValidatedFile validated = new CourseFileValidator.ValidatedFile(tempFile, "intro.mp4", "video/mp4");

        when(courses.editable(1L, 1L)).thenReturn(course);
        when(validator.validate(file, Kind.VIDEO)).thenReturn(validated);
        when(storage.upload(1L, Kind.VIDEO, tempFile, "intro.mp4", "video/mp4")).thenReturn(
            new CourseObjectStorage.Uploaded("https://r2/intro.mp4", "intro.mp4", "video/mp4", 100L)
        );
        when(courses.changed(course)).thenReturn(mockBuilder);

        service.courseMedia(1L, 1L, file, false);

        assertThat(course.getIntroVideoUrl()).isEqualTo("https://r2/intro.mp4");
        verify(cleanup).onRollback("https://r2/intro.mp4");
    }

    @Test
    void removeCourseMedia_Thumbnail_Normal() {
        course.setThumbnailUrl("https://r2/old-thumb.png");
        when(courses.editable(1L, 1L)).thenReturn(course);
        when(courses.changed(course)).thenReturn(mockBuilder);

        service.removeCourseMedia(1L, 1L, true);

        assertThat(course.getThumbnailUrl()).isNull();
        verify(cleanup).afterCommit("https://r2/old-thumb.png");
        verify(courses).changed(course);
    }

    @Test
    void removeCourseMedia_IntroVideo_Normal() {
        course.setIntroVideoUrl("https://r2/old-intro.mp4");
        when(courses.editable(1L, 1L)).thenReturn(course);
        when(courses.changed(course)).thenReturn(mockBuilder);

        service.removeCourseMedia(1L, 1L, false);

        assertThat(course.getIntroVideoUrl()).isNull();
        verify(cleanup).afterCommit("https://r2/old-intro.mp4");
    }

    @Test
    void uploadMaterial_NewDocx_Normal() throws Exception {
        Path tempFile = Files.createFile(tempDir.resolve("doc.docx"));
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "doc.docx",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            new byte[50]
        );
        CourseFileValidator.ValidatedFile validated = new CourseFileValidator.ValidatedFile(
            tempFile,
            "doc.docx",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        );

        when(courses.editable(1L, 1L)).thenReturn(course);
        when(courses.lesson(1L, 101L)).thenReturn(lesson);
        when(validator.validate(file, Kind.DOCUMENT)).thenReturn(validated);
        when(storage.upload(eq(1L), eq(Kind.DOCUMENT), any(), any(), any())).thenReturn(
            new CourseObjectStorage.Uploaded(
                "https://r2/doc.docx",
                "doc.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                50L
            )
        );
        when(materials.findByLessonIdOrderByOrderIndexAscIdAsc(101L)).thenReturn(List.of());
        when(courses.changed(course)).thenReturn(mockBuilder);

        service.uploadMaterial(1L, 101L, null, 1L, file);

        ArgumentCaptor<CourseMaterial> captor = ArgumentCaptor.forClass(CourseMaterial.class);
        verify(materials).save(captor.capture());
        CourseMaterial saved = captor.getValue();
        assertThat(saved.getFileUrl()).isEqualTo("https://r2/doc.docx");
        assertThat(saved.getFileType()).isEqualTo("DOCX");
        assertThat(saved.getOrderIndex()).isZero();
        verify(courses).changed(course);
    }

    @Test
    void uploadMaterial_ReplacePdf_Normal() throws Exception {
        Path tempFile = Files.createFile(tempDir.resolve("slide.pdf"));
        MockMultipartFile file = new MockMultipartFile("file", "slide.pdf", "application/pdf", new byte[80]);
        CourseFileValidator.ValidatedFile validated = new CourseFileValidator.ValidatedFile(tempFile, "slide.pdf", "application/pdf");

        CourseMaterial existing = new CourseMaterial().id(500L).lesson(lesson).fileUrl("https://r2/old-slide.pdf");

        when(courses.editable(1L, 1L)).thenReturn(course);
        when(courses.lesson(1L, 101L)).thenReturn(lesson);
        when(courses.material(1L, 101L, 500L)).thenReturn(existing);
        when(validator.validate(file, Kind.DOCUMENT)).thenReturn(validated);
        when(storage.upload(eq(1L), eq(Kind.DOCUMENT), any(), any(), any())).thenReturn(
            new CourseObjectStorage.Uploaded("https://r2/slide.pdf", "slide.pdf", "application/pdf", 80L)
        );

        Query query = mock(Query.class);
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("id"), any())).thenReturn(query);
        when(courses.changed(course)).thenReturn(mockBuilder);

        service.uploadMaterial(1L, 101L, 500L, 1L, file);

        assertThat(existing.getFileUrl()).isEqualTo("https://r2/slide.pdf");
        assertThat(existing.getFileType()).isEqualTo("PDF");
        verify(cleanup).afterCommit("https://r2/old-slide.pdf");
        verify(query).executeUpdate(); // deleted old PdfChunks
        verify(materials).save(existing);
    }

    @Test
    void removeLessonVideo_Success_Normal() {
        lesson.setVideoUrl("https://r2/video.mp4");
        lesson.setVideoFileName("video.mp4");
        lesson.setVideoSizeBytes(5000L);
        lesson.setDurationSeconds(120);
        lesson.setDurationMinutes(2);

        when(courses.editable(1L, 1L)).thenReturn(course);
        when(courses.lesson(1L, 101L)).thenReturn(lesson);
        when(courses.changed(course)).thenReturn(mockBuilder);

        service.removeLessonVideo(1L, 101L, 1L);

        assertThat(lesson.getVideoUrl()).isNull();
        assertThat(lesson.getVideoFileName()).isNull();
        assertThat(lesson.getVideoSizeBytes()).isNull();
        assertThat(lesson.getDurationSeconds()).isNull();
        assertThat(lesson.getDurationMinutes()).isNull();
        verify(cleanup).afterCommit("https://r2/video.mp4");
        verify(courses).changed(course);
    }

    @Test
    void updateVideoDuration_NoVideo_Abnormal() {
        lesson.setVideoUrl(null); // No video
        when(courses.editable(1L, 1L)).thenReturn(course);
        when(courses.lesson(1L, 101L)).thenReturn(lesson);

        assertThatThrownBy(() -> service.updateVideoDuration(1L, 101L, 1L, 300))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("No video attached");
    }
}
