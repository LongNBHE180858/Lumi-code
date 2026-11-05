package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PayoutRequestTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static PayoutRequest getPayoutRequestSample1() {
        return new PayoutRequest()
            .id(1L)
            .bankCode("bankCode1")
            .bankAccount("bankAccount1")
            .status("status1")
            .accountHolderName("accountHolderName1")
            .transferReference("transferReference1");
    }

    public static PayoutRequest getPayoutRequestSample2() {
        return new PayoutRequest()
            .id(2L)
            .bankCode("bankCode2")
            .bankAccount("bankAccount2")
            .status("status2")
            .accountHolderName("accountHolderName2")
            .transferReference("transferReference2");
    }

    public static PayoutRequest getPayoutRequestRandomSampleGenerator() {
        return new PayoutRequest()
            .id(longCount.incrementAndGet())
            .bankCode(UUID.randomUUID().toString())
            .bankAccount(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString())
            .accountHolderName(UUID.randomUUID().toString())
            .transferReference(UUID.randomUUID().toString());
    }
}
