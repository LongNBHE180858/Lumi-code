package com.lumi.service.dto;

import com.lumi.domain.SystemPlan;
import com.lumi.domain.enumeration.PremiumPackageType;
import java.math.BigDecimal;

public record PremiumPackageDTO(
    Long id,
    String name,
    PremiumPackageType packageType,
    BigDecimal price,
    Integer durationDays,
    Long storageQuotaMb,
    Integer aiGenerationLimit,
    Integer maxClasses,
    Integer coveredPeople,
    Integer revision,
    boolean available,
    boolean needsClassification
) {
    public static PremiumPackageDTO from(SystemPlan plan) {
        return new PremiumPackageDTO(
            plan.getId(),
            plan.getName(),
            plan.getPackageType(),
            plan.getPrice(),
            plan.getDurationDays(),
            plan.getStorageQuotaMb(),
            plan.getAiGenerationLimit(),
            plan.getMaxClasses(),
            plan.getPackageType() == PremiumPackageType.PARENT_FAMILY ? 4 : null,
            plan.getRevision(),
            Boolean.TRUE.equals(plan.getAvailable()),
            plan.getPackageType() == null
        );
    }
}
