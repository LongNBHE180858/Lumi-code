package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CourseOrderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static CourseOrder getCourseOrderSample1() {
        return new CourseOrder()
            .id(1L)
            .revenueSplitRate("revenueSplitRate1")
            .idempotencyKey("idempotencyKey1")
            .rowVersion(1L)
            .orderType("orderType1")
            .accessDurationValue(1)
            .accessDurationUnit("accessDurationUnit1")
            .entitlementStatus("entitlementStatus1");
    }

    public static CourseOrder getCourseOrderSample2() {
        return new CourseOrder()
            .id(2L)
            .revenueSplitRate("revenueSplitRate2")
            .idempotencyKey("idempotencyKey2")
            .rowVersion(2L)
            .orderType("orderType2")
            .accessDurationValue(2)
            .accessDurationUnit("accessDurationUnit2")
            .entitlementStatus("entitlementStatus2");
    }

    public static CourseOrder getCourseOrderRandomSampleGenerator() {
        return new CourseOrder()
            .id(longCount.incrementAndGet())
            .revenueSplitRate(UUID.randomUUID().toString())
            .idempotencyKey(UUID.randomUUID().toString())
            .rowVersion(longCount.incrementAndGet())
            .orderType(UUID.randomUUID().toString())
            .accessDurationValue(intCount.incrementAndGet())
            .accessDurationUnit(UUID.randomUUID().toString())
            .entitlementStatus(UUID.randomUUID().toString());
    }
}
