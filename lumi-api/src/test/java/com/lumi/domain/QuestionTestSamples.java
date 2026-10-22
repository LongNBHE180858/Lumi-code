package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class QuestionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static Question getQuestionSample1() {
        return new Question().id(1L).content("content1").type("type1").difficulty(1).explanation("explanation1");
    }

    public static Question getQuestionSample2() {
        return new Question().id(2L).content("content2").type("type2").difficulty(2).explanation("explanation2");
    }

    public static Question getQuestionRandomSampleGenerator() {
        return new Question()
            .id(longCount.incrementAndGet())
            .content(UUID.randomUUID().toString())
            .type(UUID.randomUUID().toString())
            .difficulty(intCount.incrementAndGet())
            .explanation(UUID.randomUUID().toString());
    }
}
