package com.lumi.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SePayWebhookRequestDTO {

    @NotNull @Positive Long id;
    @NotBlank @Size(max = 255) String gateway;
    @NotBlank @Size(max = 255) String accountNumber;
    @Size(max = 255) String subAccount;
    @NotBlank String transactionDate;
    @Size(max = 255) String code;
    @NotBlank String content;
    @NotBlank @Pattern(regexp = "in|out") String transferType;
    @NotNull @Positive @Digits(integer = 19, fraction = 0) BigDecimal transferAmount;
    @Size(max = 255) String referenceCode;
}
