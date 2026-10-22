package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ClassMemberTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static ClassMember getClassMemberSample1() {
        return new ClassMember().id(1L).status("status1");
    }

    public static ClassMember getClassMemberSample2() {
        return new ClassMember().id(2L).status("status2");
    }

    public static ClassMember getClassMemberRandomSampleGenerator() {
        return new ClassMember().id(longCount.incrementAndGet()).status(UUID.randomUUID().toString());
    }
}
