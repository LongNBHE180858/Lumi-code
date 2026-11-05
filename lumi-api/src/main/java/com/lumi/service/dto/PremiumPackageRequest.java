package com.lumi.service.dto;

import com.lumi.domain.enumeration.PremiumAiLimitPeriod;
import com.lumi.domain.enumeration.PremiumPackageType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PremiumPackageRequest(
    @NotBlank @Size(max = 100) String name,
    @NotNull PremiumPackageType packageType,
    @NotNull @DecimalMin("0") @Digits(integer = 19, fraction = 0) BigDecimal price,
    @NotNull @Min(1) Integer durationDays,
    @NotNull @Min(0) Long storageQuotaMb,
    @NotNull @Min(0) Integer aiGenerationLimit,
    @NotNull PremiumAiLimitPeriod aiLimitPeriod,
    @Min(1) Integer maxClasses
) {}
