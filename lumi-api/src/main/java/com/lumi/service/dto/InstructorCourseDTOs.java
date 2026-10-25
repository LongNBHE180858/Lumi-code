package com.lumi.service.dto;

import com.lumi.domain.enumeration.AccessDurationUnit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** Request DTOs intentionally exclude ownership, lifecycle and storage URLs. */
public final class InstructorCourseDTOs {

    private InstructorCourseDTOs() {}

    public record CreateDraft(@Size(max = 255) String title) {}

    public record BasicInfo(
        @NotNull @Min(0) Long version,
        @NotBlank @Size(max = 255) String title,
        Long subjectId,
        Long gradeId,
        @Size(max = 100) String bookSeries,
        @Size(max = 1000) String shortDescription,
        @Size(max = 5000) String description,
        @Size(max = 1000) String targetAudience,
        @Size(max = 1000) String prerequisites,
        List<@NotBlank String> learningObjectives
    ) {}

    public record CategoryInput(@NotNull @Min(0) Long version, Long categoryId) {}

    public record ChapterInput(@NotNull @Min(0) Long version, @NotBlank @Size(max = 255) String title) {}

    /** VND, one-time payment; zero means free. Duration uses calendar units. */
    public record Pricing(
        @NotNull @Min(0) Long version,
        @NotNull @DecimalMin("0") @Digits(integer = 19, fraction = 0) BigDecimal price,
        @NotNull @Min(1) Integer accessDurationValue,
        @NotNull AccessDurationUnit accessDurationUnit
    ) {}

    public record LessonInput(
        @NotNull @Min(0) Long version,
        @NotBlank @Size(max = 255) String title,
        String description,
        @NotNull Boolean isPreview
    ) {}

    public record ChapterOrder(@NotNull Long id, @NotNull List<@NotNull Long> lessonIds) {}

    public record MoveLesson(@NotNull @Min(0) Long version, @NotNull Long chapterId, @NotBlank @Size(max = 255) String title) {}

    public record VideoDuration(@NotNull @Min(0) Long version, @NotNull @Min(0) Integer durationSeconds) {}

    public record CurriculumOrder(@NotNull @Min(0) Long version, @NotNull List<@NotNull @Valid ChapterOrder> chapters) {}

    public record MaterialInput(@NotNull @Min(0) Long version, @Size(max = 255) String displayName) {}

    public record MaterialOrder(@NotNull @Min(0) Long version, @NotNull List<@NotNull Long> materialIds) {}

    public record LessonNode(LessonDTO lesson, List<CourseMaterialDTO> materials) {}

    public record ChapterNode(ChapterDTO chapter, List<LessonNode> lessons) {}

    public record SubmitReview(
        @NotNull @Min(0) Long version,
        @Size(max = 5000) String note,
        @NotNull @AssertTrue Boolean confirmedRights
    ) {}

    public record ReviewCheck(String code, String tab, String label, boolean required, boolean complete) {}

    public record ReviewSummary(
        Long id,
        com.lumi.domain.enumeration.CourseReviewRequestStatus status,
        Long courseVersion,
        String submissionNote,
        Instant rightsConfirmedAt,
        Instant submittedAt,
        Instant reviewedAt,
        String reviewerFeedback
    ) {}

    public record ReviewState(Long version, boolean canSubmit, List<ReviewCheck> checks, ReviewSummary request) {}

    public record Builder(
        CourseDTO course,
        List<ChapterNode> chapters,
        int totalLessons,
        int totalMaterials,
        int totalVideos,
        ReviewState review
    ) {
        public Builder(CourseDTO course, List<ChapterNode> chapters, int totalLessons, int totalMaterials, int totalVideos) {
            this(course, chapters, totalLessons, totalMaterials, totalVideos, null);
        }
    }
}
