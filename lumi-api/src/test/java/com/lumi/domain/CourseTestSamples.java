package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CourseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static Course getCourseSample1() {
        return new Course()
            .id(1L)
            .title("title1")
            .thumbnailUrl("thumbnailUrl1")
            .shortDescription("shortDescription1")
            .bookSeries("bookSeries1")
            .targetAudience("targetAudience1")
            .prerequisites("prerequisites1")
            .introVideoUrl("introVideoUrl1")
            .validDurationDays(1)
            .accessDurationValue(1)
            .accessDurationUnit(com.lumi.domain.enumeration.AccessDurationUnit.DAY)
            .version(1L)
            .storageLimitBytes(1L);
    }

    public static Course getCourseSample2() {
        return new Course()
            .id(2L)
            .title("title2")
            .thumbnailUrl("thumbnailUrl2")
            .shortDescription("shortDescription2")
            .bookSeries("bookSeries2")
            .targetAudience("targetAudience2")
            .prerequisites("prerequisites2")
            .introVideoUrl("introVideoUrl2")
            .validDurationDays(2)
            .accessDurationValue(2)
            .accessDurationUnit(com.lumi.domain.enumeration.AccessDurationUnit.DAY)
            .version(2L)
            .storageLimitBytes(2L);
    }

    public static Course getCourseRandomSampleGenerator() {
        return new Course()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .thumbnailUrl(UUID.randomUUID().toString())
            .shortDescription(UUID.randomUUID().toString())
            .bookSeries(UUID.randomUUID().toString())
            .targetAudience(UUID.randomUUID().toString())
            .prerequisites(UUID.randomUUID().toString())
            .introVideoUrl(UUID.randomUUID().toString())
            .validDurationDays(intCount.incrementAndGet())
            .accessDurationValue(intCount.incrementAndGet())
            .accessDurationUnit(com.lumi.domain.enumeration.AccessDurationUnit.DAY)
            .version(longCount.incrementAndGet())
            .storageLimitBytes(longCount.incrementAndGet());
    }
}
