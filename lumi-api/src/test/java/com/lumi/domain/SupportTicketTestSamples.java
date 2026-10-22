package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SupportTicketTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static SupportTicket getSupportTicketSample1() {
        return new SupportTicket().id(1L).type("type1").subject("subject1").status("status1");
    }

    public static SupportTicket getSupportTicketSample2() {
        return new SupportTicket().id(2L).type("type2").subject("subject2").status("status2");
    }

    public static SupportTicket getSupportTicketRandomSampleGenerator() {
        return new SupportTicket()
            .id(longCount.incrementAndGet())
            .type(UUID.randomUUID().toString())
            .subject(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString());
    }
}
