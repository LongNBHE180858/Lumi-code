package com.lumi.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.domain.*;
import com.lumi.domain.enumeration.*;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class CourseBuilderMappingTest {

    @Test
    void courseRoundTripPreservesObjectivesOrderAndAccessUnit() {
        CourseMapper mapper = new CourseMapperImpl();
        Course course = new Course()
            .title("Math")
            .shortDescription("Summary")
            .bookSeries("Kết nối tri thức với cuộc sống")
            .targetAudience("Grade 6")
            .prerequisites("Grade 5")
            .learningObjectives(List.of("First", "Second"))
            .introVideoUrl("https://media.example/intro.mp4")
            .accessDurationValue(12)
            .accessDurationUnit(AccessDurationUnit.MONTH)
            .version(3L);
        Course result = mapper.toEntity(mapper.toDto(course));
        assertThat(result).usingRecursiveComparison().isEqualTo(course);
    }

    @Test
    void documentLessonDoesNotRequireVideo() {
        LessonMapper mapper = new LessonMapperImpl();
        Lesson lesson = new Lesson()
            .title("Read this")
            .contentType("DOCUMENT")
            .orderIndex(0)
            .description("Read the attached PDF and DOCX.");
        Lesson result = mapper.toEntity(mapper.toDto(lesson));
        assertThat(result).usingRecursiveComparison().isEqualTo(lesson);
        assertThat(result.getVideoUrl()).isNull();
    }

    @Test
    void materialRoundTripPreservesCloudUrlAndPresentation() {
        CourseMaterialMapper mapper = new CourseMaterialMapperImpl();
        CourseMaterial material = new CourseMaterial()
            .fileType("PDF")
            .fileUrl("https://media.example/lesson.pdf")
            .fileName("lesson.pdf")
            .displayName("Lesson notes")
            .mimeType("application/pdf")
            .orderIndex(2)
            .fileSizeBytes(123L);
        assertThat(mapper.toEntity(mapper.toDto(material)))
            .usingRecursiveComparison()
            .isEqualTo(material);
    }

    @Test
    void reviewRequestIsSeparateFromStudentRating() {
        CourseReviewRequestMapper mapper = new CourseReviewRequestMapperImpl();
        CourseReviewRequest request = new CourseReviewRequest();
        request.setCourse(new Course().id(7L).title("Math"));
        User owner = new User();
        owner.setId(9L);
        owner.setLogin("teacher");
        request.setSubmittedBy(owner);
        request.setCourseVersion(3L);
        request.setStatus(CourseReviewRequestStatus.PENDING);
        request.setSubmissionNote("Please review");
        request.setRightsConfirmedAt(Instant.parse("2026-10-19T00:00:00Z"));
        request.setSubmittedAt(request.getRightsConfirmedAt());
        var dto = mapper.toDto(request);
        var result = mapper.toEntity(dto);
        assertThat(result.getCourse().getId()).isEqualTo(7L);
        assertThat(result.getSubmittedBy().getId()).isEqualTo(9L);
        assertThat(result.getCourseVersion()).isEqualTo(3L);
        assertThat(result.getStatus()).isEqualTo(CourseReviewRequestStatus.PENDING);
        assertThat(result.getSubmissionNote()).isEqualTo("Please review");
        assertThat(result.getRightsConfirmedAt()).isEqualTo(request.getRightsConfirmedAt());
        assertThat(result.getReviewedBy()).isNull();
    }
}
