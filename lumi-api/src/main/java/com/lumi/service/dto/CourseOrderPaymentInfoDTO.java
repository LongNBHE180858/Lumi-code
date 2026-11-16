package com.lumi.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lumi.domain.enumeration.OrderStatus;
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
public class CourseOrderPaymentInfoDTO {

    Long orderId;
    OrderStatus status;
    boolean paymentRequired;
    Instant expiresAt;
    Instant serverTime;
    PaymentInfoDTO paymentInfo;
}
