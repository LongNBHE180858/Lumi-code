package com.lumi.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

/** Public course information. Curriculum entries intentionally contain no material URLs. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.ALWAYS)
public class CourseOverviewResponse {

    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private Long subjectId;
    private String subjectName;
    private Long gradeId;
    private Integer gradeLevel;
    private String gradeName;
    private List<TagSummaryDTO> tags;
    private BigDecimal price;
    private Integer validDurationDays;

    /** Public author information only; populate according to profile visibility rules. */
    private InstructorPublicProfileDTO author;

    private long chapterCount;
    private long lessonCount;
    private CourseRatingSummaryDTO ratingSummary;

    @Builder.Default
    private List<ChapterItem> chapters = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class ChapterItem {

        private Long id;
        private String title;
        private Integer orderIndex;
        private long lessonCount;

        @Builder.Default
        private List<LessonItem> lessons = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class LessonItem {

        private Long id;
        private String title;
        private Integer orderIndex;

        /** Uses the existing Lesson.contentType values. */
        private String contentType;

        /** Null when no duration is defined, for example for a PDF lesson. */
        private Integer durationMinutes;
    }
}
