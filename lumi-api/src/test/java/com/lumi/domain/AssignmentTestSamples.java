package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AssignmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Assignment getAssignmentSample1() {
        return new Assignment().id(1L).title("title1").status("status1");
    }

    public static Assignment getAssignmentSample2() {
        return new Assignment().id(2L).title("title2").status("status2");
    }

    public static Assignment getAssignmentRandomSampleGenerator() {
        return new Assignment().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString()).status(UUID.randomUUID().toString());
    }
}
