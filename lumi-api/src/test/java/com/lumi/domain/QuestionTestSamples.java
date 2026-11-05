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
        return new Question()
            .id(1L)
            .type("type1")
            .difficulty(1)
            .sourceType("sourceType1")
            .contentHash("contentHash1")
            .visibility("visibility1")
            .version(1L);
    }

    public static Question getQuestionSample2() {
        return new Question()
            .id(2L)
            .type("type2")
            .difficulty(2)
            .sourceType("sourceType2")
            .contentHash("contentHash2")
            .visibility("visibility2")
            .version(2L);
    }

    public static Question getQuestionRandomSampleGenerator() {
        return new Question()
            .id(longCount.incrementAndGet())
            .type(UUID.randomUUID().toString())
            .difficulty(intCount.incrementAndGet())
            .sourceType(UUID.randomUUID().toString())
            .contentHash(UUID.randomUUID().toString())
            .visibility(UUID.randomUUID().toString())
            .version(longCount.incrementAndGet());
    }
}
