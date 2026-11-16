package com.lumi.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lumi.domain.enumeration.AccessDurationUnit;
import java.math.BigDecimal;
import lombok.*;

/** Checkout preview. Loading this DTO must not create an order. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.ALWAYS)
public class CourseCheckoutDTO {

    private Long courseId;
    private Long studentId;
    private String title;
    private String thumbnailUrl;
    private Long subjectId;
    private String subjectName;
    private Long gradeId;
    private Integer gradeLevel;
    private String gradeName;

    /** Normalize legacy validDurationDays to value + DAY in the service. */
    private Integer accessDurationValue;
    private AccessDurationUnit accessDurationUnit;

    /** VND amounts calculated by the server; tax is temporarily zero. */
    private BigDecimal originalPrice;
    private BigDecimal taxAmount;
    private BigDecimal finalPrice;

    private boolean canPurchase;

    /** Vietnamese explanation when purchase is blocked; null when allowed. */
    private String purchaseBlockReason;
}
