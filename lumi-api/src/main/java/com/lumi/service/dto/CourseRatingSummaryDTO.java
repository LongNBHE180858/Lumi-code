package com.lumi.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import lombok.*;

/** Aggregates all public reviews of a course, independently of review pagination. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.ALWAYS)
public class CourseRatingSummaryDTO {

    /** Null when totalReviews is zero; an unrated course does not have a zero-star rating. */
    private BigDecimal averageRating;

    private long totalReviews;
    private long oneStarCount;
    private long twoStarCount;
    private long threeStarCount;
    private long fourStarCount;
    private long fiveStarCount;
}
