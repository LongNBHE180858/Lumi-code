package com.lumi.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.IntegrationTest;
import com.lumi.domain.*;
import com.lumi.domain.enumeration.*;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class CourseBuilderSchemaIT {

    @Autowired
    private EntityManager em;

    @Test
    void persistsJsonObjectivesDocumentLessonAndReviewRequest() {
        User teacher = new User();
        teacher.setLogin("builder-schema-test");
        teacher.setPassword("x".repeat(60));
        em.persist(teacher);
        Course course = new Course()
            .title("Draft")
            .author(teacher)
            .status(RecordStatus.DRAFT)
            .learningObjectives(List.of("Đọc tài liệu", "Làm bài tập"))
            .accessDurationValue(12)
            .accessDurationUnit(AccessDurationUnit.MONTH);
        em.persist(course);
        Chapter chapter = new Chapter().title("Chapter").course(course).orderIndex(0);
        em.persist(chapter);
        Lesson lesson = new Lesson()
            .chapter(chapter)
            .title("Documents")
            .contentType("DOCUMENT")
            .orderIndex(0)
            .description("Read these files");
        em.persist(lesson);
        CourseMaterial material = new CourseMaterial()
            .lesson(lesson)
            .fileType("DOCX")
            .fileUrl("https://media.example/notes.docx")
            .fileName("notes.docx")
            .mimeType("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
            .orderIndex(0);
        em.persist(material);
        em.flush();
        CourseReviewRequest request = new CourseReviewRequest();
        request.setCourse(course);
        request.setSubmittedBy(teacher);
        request.setCourseVersion(course.getVersion());
        request.setStatus(CourseReviewRequestStatus.PENDING);
        request.setRightsConfirmedAt(Instant.now());
        request.setSubmittedAt(Instant.now());
        em.persist(request);
        em.flush();
        em.clear();
        Course loaded = em.find(Course.class, course.getId());
        assertThat(loaded.getLearningObjectives()).containsExactly("Đọc tài liệu", "Làm bài tập");
        assertThat(loaded.getValidDurationDays()).isNull();
        assertThat(loaded.getAccessDurationUnit()).isEqualTo(AccessDurationUnit.MONTH);
        assertThat(em.find(Lesson.class, lesson.getId()).getVideoUrl()).isNull();
        assertThat(em.find(CourseMaterial.class, material.getId()).getFileName()).isEqualTo("notes.docx");
        assertThat(em.find(CourseReviewRequest.class, request.getId()).getCourse().getId()).isEqualTo(course.getId());
    }
}
