package com.lumi.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lumi.domain.*;
import com.lumi.repository.LessonRepository;
import com.lumi.service.dto.InstructorCourseDTOs.*;
import com.lumi.service.impl.InstructorCourseService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class InstructorLessonMoveTest {

    @Mock
    LessonRepository lessons;

    @Spy
    @InjectMocks
    InstructorCourseService service;

    private final Course course = new Course().id(1L);
    private final Chapter source = new Chapter().id(10L);
    private final Chapter target = new Chapter().id(20L);
    private Lesson moving;

    @BeforeEach
    void setup() {
        moving = new Lesson().id(101L).title("Bài 1").chapter(source).orderIndex(0);
        doReturn(course).when(service).editable(1L, 1L);
        doReturn(moving).when(service).lesson(1L, 101L);
        doReturn(target).when(service).chapter(1L, 20L);
    }

    @Test
    void movesAndRenamesTogetherWithConfirmedTitle() {
        Lesson existing = new Lesson().id(201L).title("BÀI 1").orderIndex(0);
        Lesson second = new Lesson().id(202L).title("Bài 1 (1)").orderIndex(1);
        Lesson remaining = new Lesson().id(102L).title("Other").orderIndex(1);
        when(lessons.findByChapterIdOrderByOrderIndexAscIdAsc(20L)).thenReturn(List.of(existing, second));
        when(lessons.findByChapterIdOrderByOrderIndexAscIdAsc(10L)).thenReturn(List.of(moving, remaining));
        Builder result = new Builder(null, List.of(), 1, 0, 0);
        doReturn(result).when(service).changed(course);
        assertThat(service.moveLesson(1L, 101L, new MoveLesson(1L, 20L, "Bài 1 (2)"))).isSameAs(result);
        assertThat(moving.getTitle()).isEqualTo("Bài 1 (2)");
        assertThat(moving.getChapter()).isSameAs(target);
        assertThat(moving.getOrderIndex()).isEqualTo(2);
        assertThat(remaining.getOrderIndex()).isZero();
    }

    @Test
    void rejectsUnconfirmedRenameWithoutChangingTitleOrChapter() {
        when(lessons.findByChapterIdOrderByOrderIndexAscIdAsc(20L)).thenReturn(List.of(new Lesson().title("Bài 1")));
        assertThatThrownBy(() -> service.moveLesson(1L, 101L, new MoveLesson(1L, 20L, "Bài 1"))).isInstanceOfSatisfying(
            ResponseStatusException.class,
            error -> assertThat(error.getStatusCode().value()).isEqualTo(409)
        );
        assertThat(moving.getTitle()).isEqualTo("Bài 1");
        assertThat(moving.getChapter()).isSameAs(source);
        verify(service, never()).changed(any());
    }

    @Test
    void keepsTitleWhenDestinationHasNoDuplicate() {
        when(lessons.findByChapterIdOrderByOrderIndexAscIdAsc(20L)).thenReturn(List.of());
        when(lessons.findByChapterIdOrderByOrderIndexAscIdAsc(10L)).thenReturn(List.of(moving));
        doReturn(new Builder(null, List.of(), 1, 0, 0))
            .when(service)
            .changed(course);
        service.moveLesson(1L, 101L, new MoveLesson(1L, 20L, "Bài 1"));
        assertThat(moving.getTitle()).isEqualTo("Bài 1");
        assertThat(moving.getOrderIndex()).isZero();
    }
}
