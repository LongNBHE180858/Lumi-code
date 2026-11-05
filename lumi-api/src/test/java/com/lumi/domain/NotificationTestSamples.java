package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class NotificationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Notification getNotificationSample1() {
        return new Notification().id(1L).title("title1").type("type1").targetType("targetType1").targetId(1L);
    }

    public static Notification getNotificationSample2() {
        return new Notification().id(2L).title("title2").type("type2").targetType("targetType2").targetId(2L);
    }

    public static Notification getNotificationRandomSampleGenerator() {
        return new Notification()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .type(UUID.randomUUID().toString())
            .targetType(UUID.randomUUID().toString())
            .targetId(longCount.incrementAndGet());
    }
}
