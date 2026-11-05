package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LessonTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static Lesson getLessonSample1() {
        return new Lesson()
            .id(1L)
            .title("title1")
            .contentType("contentType1")
            .videoUrl("videoUrl1")
            .videoFileName("videoFileName1")
            .videoSizeBytes(1L)
            .orderIndex(1)
            .durationMinutes(1)
            .durationSeconds(1)
            .videoObjectKey("videoObjectKey1");
    }

    public static Lesson getLessonSample2() {
        return new Lesson()
            .id(2L)
            .title("title2")
            .contentType("contentType2")
            .videoUrl("videoUrl2")
            .videoFileName("videoFileName2")
            .videoSizeBytes(2L)
            .orderIndex(2)
            .durationMinutes(2)
            .durationSeconds(2)
            .videoObjectKey("videoObjectKey2");
    }

    public static Lesson getLessonRandomSampleGenerator() {
        return new Lesson()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .contentType(UUID.randomUUID().toString())
            .videoUrl(UUID.randomUUID().toString())
            .videoFileName(UUID.randomUUID().toString())
            .videoSizeBytes(longCount.incrementAndGet())
            .orderIndex(intCount.incrementAndGet())
            .durationMinutes(intCount.incrementAndGet())
            .durationSeconds(intCount.incrementAndGet())
            .videoObjectKey(UUID.randomUUID().toString());
    }
}
