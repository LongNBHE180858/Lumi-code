package com.lumi.domain;

import java.math.BigDecimal;

public final class SystemPlanTestFixtures {

    private SystemPlanTestFixtures() {}

    public static SystemPlan createEntity() {
        return new SystemPlan()
            .name("Test plan")
            .price(BigDecimal.ZERO)
            .durationDays(30)
            .storageQuotaMb(0L)
            .aiGenerationLimit(0)
            .maxClasses(0)
            .planType("PERSONAL")
            .targetRole("PARENT")
            .status("UNAVAILABLE")
            .aiLimitPeriod("PACKAGE_TERM")
            .features("[]")
            .updatedAt(java.time.Instant.now());
    }
}
