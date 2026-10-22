package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ExamAnswerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static ExamAnswer getExamAnswerSample1() {
        return new ExamAnswer().id(1L).textAnswer("textAnswer1");
    }

    public static ExamAnswer getExamAnswerSample2() {
        return new ExamAnswer().id(2L).textAnswer("textAnswer2");
    }

    public static ExamAnswer getExamAnswerRandomSampleGenerator() {
        return new ExamAnswer().id(longCount.incrementAndGet()).textAnswer(UUID.randomUUID().toString());
    }
}
