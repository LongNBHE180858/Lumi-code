package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SystemPlanTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static SystemPlan getSystemPlanSample1() {
        return new SystemPlan().id(1L).name("name1").durationDays(1).storageQuotaMb(1L).aiGenerationLimit(1).maxClasses(1);
    }

    public static SystemPlan getSystemPlanSample2() {
        return new SystemPlan().id(2L).name("name2").durationDays(2).storageQuotaMb(2L).aiGenerationLimit(2).maxClasses(2);
    }

    public static SystemPlan getSystemPlanRandomSampleGenerator() {
        return new SystemPlan()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .durationDays(intCount.incrementAndGet())
            .storageQuotaMb(longCount.incrementAndGet())
            .aiGenerationLimit(intCount.incrementAndGet())
            .maxClasses(intCount.incrementAndGet());
    }
}
