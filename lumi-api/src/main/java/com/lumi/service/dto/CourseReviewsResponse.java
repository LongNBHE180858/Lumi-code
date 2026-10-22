package com.lumi.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

/** Public review page; never exposes the student's account DTO or private profile. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.ALWAYS)
public class CourseReviewsResponse {

    private Long courseId;
    private CourseRatingSummaryDTO ratingSummary;

    @Builder.Default
    private List<ReviewItem> content = new ArrayList<>();

    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class ReviewItem {

        private Long id;
        private String displayName;
        private String imageUrl;
        private Integer rating;
        private String content;
        private Instant createdAt;
    }
}
