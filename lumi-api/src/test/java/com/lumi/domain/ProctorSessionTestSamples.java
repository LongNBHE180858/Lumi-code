package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ProctorSessionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static ProctorSession getProctorSessionSample1() {
        return new ProctorSession().id(1L).warningCount(1).status("status1");
    }

    public static ProctorSession getProctorSessionSample2() {
        return new ProctorSession().id(2L).warningCount(2).status("status2");
    }

    public static ProctorSession getProctorSessionRandomSampleGenerator() {
        return new ProctorSession()
            .id(longCount.incrementAndGet())
            .warningCount(intCount.incrementAndGet())
            .status(UUID.randomUUID().toString());
    }
}
