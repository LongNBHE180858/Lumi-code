package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class GradeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static Grade getGradeSample1() {
        return new Grade().id(1L).level(1).name("name1");
    }

    public static Grade getGradeSample2() {
        return new Grade().id(2L).level(2).name("name2");
    }

    public static Grade getGradeRandomSampleGenerator() {
        return new Grade().id(longCount.incrementAndGet()).level(intCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
