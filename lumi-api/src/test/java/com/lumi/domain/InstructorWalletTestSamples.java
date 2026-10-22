package com.lumi.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class InstructorWalletTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static InstructorWallet getInstructorWalletSample1() {
        return new InstructorWallet().id(1L).rowVersion(1L);
    }

    public static InstructorWallet getInstructorWalletSample2() {
        return new InstructorWallet().id(2L).rowVersion(2L);
    }

    public static InstructorWallet getInstructorWalletRandomSampleGenerator() {
        return new InstructorWallet().id(longCount.incrementAndGet()).rowVersion(longCount.incrementAndGet());
    }
}
