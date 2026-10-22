package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TicketMessageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static TicketMessage getTicketMessageSample1() {
        return new TicketMessage().id(1L).content("content1");
    }

    public static TicketMessage getTicketMessageSample2() {
        return new TicketMessage().id(2L).content("content2");
    }

    public static TicketMessage getTicketMessageRandomSampleGenerator() {
        return new TicketMessage().id(longCount.incrementAndGet()).content(UUID.randomUUID().toString());
    }
}
