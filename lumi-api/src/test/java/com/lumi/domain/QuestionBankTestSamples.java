package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class QuestionBankTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static QuestionBank getQuestionBankSample1() {
        return new QuestionBank().id(1L).name("name1").visibility("visibility1");
    }

    public static QuestionBank getQuestionBankSample2() {
        return new QuestionBank().id(2L).name("name2").visibility("visibility2");
    }

    public static QuestionBank getQuestionBankRandomSampleGenerator() {
        return new QuestionBank()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .visibility(UUID.randomUUID().toString());
    }
}
