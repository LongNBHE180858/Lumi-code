package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LessonProgressTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static LessonProgress getLessonProgressSample1() {
        return new LessonProgress().id(1L).status("status1").rowVersion(1L);
    }

    public static LessonProgress getLessonProgressSample2() {
        return new LessonProgress().id(2L).status("status2").rowVersion(2L);
    }

    public static LessonProgress getLessonProgressRandomSampleGenerator() {
        return new LessonProgress()
            .id(longCount.incrementAndGet())
            .status(UUID.randomUUID().toString())
            .rowVersion(longCount.incrementAndGet());
    }
}
