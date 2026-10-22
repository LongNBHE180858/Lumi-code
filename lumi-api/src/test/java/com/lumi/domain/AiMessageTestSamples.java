package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AiMessageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static AiMessage getAiMessageSample1() {
        return new AiMessage().id(1L).senderRole("senderRole1").content("content1");
    }

    public static AiMessage getAiMessageSample2() {
        return new AiMessage().id(2L).senderRole("senderRole2").content("content2");
    }

    public static AiMessage getAiMessageRandomSampleGenerator() {
        return new AiMessage()
            .id(longCount.incrementAndGet())
            .senderRole(UUID.randomUUID().toString())
            .content(UUID.randomUUID().toString());
    }
}
