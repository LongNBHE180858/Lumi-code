package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class QuestionOptionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static QuestionOption getQuestionOptionSample1() {
        return new QuestionOption().id(1L).content("content1");
    }

    public static QuestionOption getQuestionOptionSample2() {
        return new QuestionOption().id(2L).content("content2");
    }

    public static QuestionOption getQuestionOptionRandomSampleGenerator() {
        return new QuestionOption().id(longCount.incrementAndGet()).content(UUID.randomUUID().toString());
    }
}
