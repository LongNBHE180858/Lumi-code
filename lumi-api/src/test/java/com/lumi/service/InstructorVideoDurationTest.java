package com.lumi.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lumi.domain.*;
import com.lumi.service.dto.InstructorCourseDTOs.Builder;
import com.lumi.service.impl.InstructorCourseMediaService;
import com.lumi.service.impl.InstructorCourseService;
import com.lumi.service.storage.*;
import java.nio.file.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class InstructorVideoDurationTest {

    @Mock
    InstructorCourseService courses;

    @Mock
    CourseFileValidator validator;

    @Mock
    CourseObjectStorage storage;

    @Mock
    CourseStorageCleanup cleanup;

    @InjectMocks
    InstructorCourseMediaService service;

    @TempDir
    Path directory;

    @Test
    void uploadStoresSecondsAndReplacementWithoutMetadataClearsOldDuration() throws Exception {
        Course course = new Course().id(1L);
        Lesson lesson = new Lesson().id(101L);
        var file = new MockMultipartFile("file", "video.mp4", "video/mp4", new byte[] { 1 });
        Path path = Files.write(directory.resolve("video.mp4"), new byte[] { 1 });
        when(courses.editable(1L, 1L)).thenReturn(course);
        when(courses.lesson(1L, 101L)).thenReturn(lesson);
        when(validator.validate(file, CourseFileValidator.Kind.VIDEO)).thenReturn(
            new CourseFileValidator.ValidatedFile(path, "video.mp4", "video/mp4")
        );
        when(storage.upload(1L, CourseFileValidator.Kind.VIDEO, path, "video.mp4", "video/mp4")).thenReturn(
            new CourseObjectStorage.Uploaded("stored/new.mp4", "video.mp4", "video/mp4", 1L)
        );
        service.lessonVideo(1L, 101L, 1L, file, 125);
        assertThat(lesson.getDurationSeconds()).isEqualTo(125);
        assertThat(lesson.getDurationMinutes()).isEqualTo(2);
        service.lessonVideo(1L, 101L, 1L, file, null);
        assertThat(lesson.getDurationSeconds()).isNull();
        assertThat(lesson.getDurationMinutes()).isNull();
    }

    @Test
    void backfillsExistingVideoAndRemovalClearsBothDurationFields() {
        Course course = new Course().id(1L);
        Lesson lesson = new Lesson().id(101L).videoUrl("stored/video.mp4");
        when(courses.editable(1L, 1L)).thenReturn(course);
        when(courses.lesson(1L, 101L)).thenReturn(lesson);
        when(courses.changed(course)).thenReturn(new Builder(null, List.of(), 1, 0, 1));
        service.updateVideoDuration(1L, 101L, 1L, 61);
        assertThat(lesson.getDurationSeconds()).isEqualTo(61);
        assertThat(lesson.getDurationMinutes()).isEqualTo(1);
        service.removeLessonVideo(1L, 101L, 1L);
        assertThat(lesson.getDurationSeconds()).isNull();
        assertThat(lesson.getDurationMinutes()).isNull();
    }

    @Test
    void doesNotSaveDurationForLessonWithoutVideo() {
        when(courses.editable(1L, 1L)).thenReturn(new Course().id(1L));
        when(courses.lesson(1L, 101L)).thenReturn(new Lesson().id(101L));
        assertThatThrownBy(() -> service.updateVideoDuration(1L, 101L, 1L, 60)).isInstanceOf(ResponseStatusException.class);
        verify(courses, never()).changed(any());
    }
}
