package com.lumi.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ExamQuestionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static ExamQuestion getExamQuestionSample1() {
        return new ExamQuestion().id(1L).orderIndex(1);
    }

    public static ExamQuestion getExamQuestionSample2() {
        return new ExamQuestion().id(2L).orderIndex(2);
    }

    public static ExamQuestion getExamQuestionRandomSampleGenerator() {
        return new ExamQuestion().id(longCount.incrementAndGet()).orderIndex(intCount.incrementAndGet());
    }
}
