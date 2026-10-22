package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CourseOrderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static CourseOrder getCourseOrderSample1() {
        return new CourseOrder().id(1L).revenueSplitRate("revenueSplitRate1").idempotencyKey("idempotencyKey1").rowVersion(1L);
    }

    public static CourseOrder getCourseOrderSample2() {
        return new CourseOrder().id(2L).revenueSplitRate("revenueSplitRate2").idempotencyKey("idempotencyKey2").rowVersion(2L);
    }

    public static CourseOrder getCourseOrderRandomSampleGenerator() {
        return new CourseOrder()
            .id(longCount.incrementAndGet())
            .revenueSplitRate(UUID.randomUUID().toString())
            .idempotencyKey(UUID.randomUUID().toString())
            .rowVersion(longCount.incrementAndGet());
    }
}
