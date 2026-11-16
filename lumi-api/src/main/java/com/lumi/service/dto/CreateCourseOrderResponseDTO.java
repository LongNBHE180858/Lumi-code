package com.lumi.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lumi.domain.enumeration.AccessDurationUnit;
import com.lumi.domain.enumeration.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.*;

/** Saved order information returned after creation or an idempotent retry. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.ALWAYS)
public class CreateCourseOrderResponseDTO {

    private Long orderId;
    private Long courseId;
    private Long buyerId;
    private Long studentId;
    private OrderStatus status;

    /** Read from the order snapshot, not the current course price. */
    private BigDecimal originalPrice;
    private BigDecimal taxAmount;
    private BigDecimal finalPrice;

    private Integer accessDurationValue;
    private AccessDurationUnit accessDurationUnit;

    /** Whether the paid-course order is still pending and within its payment window. */
    private boolean paymentRequired;

    private Instant createdAt;

    /** Payment deadline, not the course-access expiry. */
    private Instant expiresAt;

    /** Server time used to anchor the payment countdown. */
    private Instant serverTime;
}
