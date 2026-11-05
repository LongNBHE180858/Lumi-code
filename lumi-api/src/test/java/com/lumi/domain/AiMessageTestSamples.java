package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AiMessageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static AiMessage getAiMessageSample1() {
        return new AiMessage()
            .id(1L)
            .senderRole("senderRole1")
            .generationStatus("generationStatus1")
            .modelName("modelName1")
            .feedbackRating(1);
    }

    public static AiMessage getAiMessageSample2() {
        return new AiMessage()
            .id(2L)
            .senderRole("senderRole2")
            .generationStatus("generationStatus2")
            .modelName("modelName2")
            .feedbackRating(2);
    }

    public static AiMessage getAiMessageRandomSampleGenerator() {
        return new AiMessage()
            .id(longCount.incrementAndGet())
            .senderRole(UUID.randomUUID().toString())
            .generationStatus(UUID.randomUUID().toString())
            .modelName(UUID.randomUUID().toString())
            .feedbackRating(intCount.incrementAndGet());
    }
}
