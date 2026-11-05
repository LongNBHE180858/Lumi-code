package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class WalletTransactionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static WalletTransaction getWalletTransactionSample1() {
        return new WalletTransaction().id(1L).status("status1").idempotencyKey("idempotencyKey1").balanceBucket("balanceBucket1");
    }

    public static WalletTransaction getWalletTransactionSample2() {
        return new WalletTransaction().id(2L).status("status2").idempotencyKey("idempotencyKey2").balanceBucket("balanceBucket2");
    }

    public static WalletTransaction getWalletTransactionRandomSampleGenerator() {
        return new WalletTransaction()
            .id(longCount.incrementAndGet())
            .status(UUID.randomUUID().toString())
            .idempotencyKey(UUID.randomUUID().toString())
            .balanceBucket(UUID.randomUUID().toString());
    }
}
