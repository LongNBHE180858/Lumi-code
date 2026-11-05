package com.lumi.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TeacherQuotaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static TeacherQuota getTeacherQuotaSample1() {
        return new TeacherQuota().id(1L).usedStorageBytes(1L).aiUsedCount(1).rowVersion(1L).effectiveStorageLimitBytes(1L);
    }

    public static TeacherQuota getTeacherQuotaSample2() {
        return new TeacherQuota().id(2L).usedStorageBytes(2L).aiUsedCount(2).rowVersion(2L).effectiveStorageLimitBytes(2L);
    }

    public static TeacherQuota getTeacherQuotaRandomSampleGenerator() {
        return new TeacherQuota()
            .id(longCount.incrementAndGet())
            .usedStorageBytes(longCount.incrementAndGet())
            .aiUsedCount(intCount.incrementAndGet())
            .rowVersion(longCount.incrementAndGet())
            .effectiveStorageLimitBytes(longCount.incrementAndGet());
    }
}
