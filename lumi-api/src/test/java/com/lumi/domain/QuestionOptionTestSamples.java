package com.lumi.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class QuestionOptionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static QuestionOption getQuestionOptionSample1() {
        return new QuestionOption().id(1L).orderIndex(1);
    }

    public static QuestionOption getQuestionOptionSample2() {
        return new QuestionOption().id(2L).orderIndex(2);
    }

    public static QuestionOption getQuestionOptionRandomSampleGenerator() {
        return new QuestionOption().id(longCount.incrementAndGet()).orderIndex(intCount.incrementAndGet());
    }
}
