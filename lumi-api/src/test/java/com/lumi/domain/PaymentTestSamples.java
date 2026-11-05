package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Payment getPaymentSample1() {
        return new Payment()
            .id(1L)
            .gateway("gateway1")
            .transactionNo("transactionNo1")
            .status("status1")
            .paymentReference("paymentReference1")
            .providerEventId("providerEventId1");
    }

    public static Payment getPaymentSample2() {
        return new Payment()
            .id(2L)
            .gateway("gateway2")
            .transactionNo("transactionNo2")
            .status("status2")
            .paymentReference("paymentReference2")
            .providerEventId("providerEventId2");
    }

    public static Payment getPaymentRandomSampleGenerator() {
        return new Payment()
            .id(longCount.incrementAndGet())
            .gateway(UUID.randomUUID().toString())
            .transactionNo(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString())
            .paymentReference(UUID.randomUUID().toString())
            .providerEventId(UUID.randomUUID().toString());
    }
}
