package com.lumi.service.dto;

import com.lumi.domain.SystemPlan;
import com.lumi.domain.enumeration.PremiumPackageStatus;
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
    boolean needsClassification,
    String aiLimitPeriod
) {
    public static PremiumPackageDTO from(SystemPlan plan, int revision) {
        PremiumPackageType type = PremiumPackageType.from(plan.getPlanType(), plan.getTargetRole());
        return new PremiumPackageDTO(
            plan.getId(),
            plan.getName(),
            type,
            plan.getPrice(),
            plan.getDurationDays(),
            plan.getStorageQuotaMb(),
            plan.getAiGenerationLimit(),
            type == PremiumPackageType.INSTRUCTOR_CLASS || type == PremiumPackageType.INSTRUCTOR_PERSONAL ? plan.getMaxClasses() : null,
            type == PremiumPackageType.PARENT_FAMILY ? 4 : null,
            revision,
            PremiumPackageStatus.AVAILABLE.name().equals(plan.getStatus()),
            type == null,
            plan.getAiLimitPeriod()
        );
    }
}
