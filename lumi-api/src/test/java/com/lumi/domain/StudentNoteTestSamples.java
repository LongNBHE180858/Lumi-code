package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class StudentNoteTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static StudentNote getStudentNoteSample1() {
        return new StudentNote().id(1L).videoTimestamp(1).content("content1");
    }

    public static StudentNote getStudentNoteSample2() {
        return new StudentNote().id(2L).videoTimestamp(2).content("content2");
    }

    public static StudentNote getStudentNoteRandomSampleGenerator() {
        return new StudentNote()
            .id(longCount.incrementAndGet())
            .videoTimestamp(intCount.incrementAndGet())
            .content(UUID.randomUUID().toString());
    }
}
