package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AiConversationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static AiConversation getAiConversationSample1() {
        return new AiConversation().id(1L).type("type1");
    }

    public static AiConversation getAiConversationSample2() {
        return new AiConversation().id(2L).type("type2");
    }

    public static AiConversation getAiConversationRandomSampleGenerator() {
        return new AiConversation().id(longCount.incrementAndGet()).type(UUID.randomUUID().toString());
    }
}
