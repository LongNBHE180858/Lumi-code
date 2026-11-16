package com.lumi.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.ALWAYS)
public class PaymentInfoDTO {

    String bankCode;
    String bankName;
    String accountNumber;
    String accountName;
    BigDecimal amount;
    String currency;
    String transferContent;
    String qrImageUrl;
}
