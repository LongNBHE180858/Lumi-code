package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DiscussionReplyTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static DiscussionReply getDiscussionReplySample1() {
        return new DiscussionReply().id(1L).content("content1");
    }

    public static DiscussionReply getDiscussionReplySample2() {
        return new DiscussionReply().id(2L).content("content2");
    }

    public static DiscussionReply getDiscussionReplyRandomSampleGenerator() {
        return new DiscussionReply().id(longCount.incrementAndGet()).content(UUID.randomUUID().toString());
    }
}
