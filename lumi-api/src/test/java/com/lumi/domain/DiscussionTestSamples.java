package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DiscussionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Discussion getDiscussionSample1() {
        return new Discussion().id(1L).title("title1").postType("postType1");
    }

    public static Discussion getDiscussionSample2() {
        return new Discussion().id(2L).title("title2").postType("postType2");
    }

    public static Discussion getDiscussionRandomSampleGenerator() {
        return new Discussion().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString()).postType(UUID.randomUUID().toString());
    }
}
