package com.lumi.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lumi.domain.enumeration.AccessDurationUnit;
import com.lumi.domain.enumeration.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.ALWAYS)
public class CourseOrderDetailDTO {

    Long orderId;
    Long buyerId;
    Long studentId;
    Long courseId;
    OrderStatus status;
    BigDecimal originalPrice;
    BigDecimal taxAmount;
    BigDecimal finalPrice;
    Integer accessDurationValue;
    AccessDurationUnit accessDurationUnit;
    boolean paymentRequired;
    Instant createdAt;
    Instant expiresAt;
    Instant paidAt;
    Instant serverTime;
}
