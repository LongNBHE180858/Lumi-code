package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AuditLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static AuditLog getAuditLogSample1() {
        return new AuditLog().id(1L).action("action1").targetEntity("targetEntity1").targetId("targetId1").ipAddress("ipAddress1");
    }

    public static AuditLog getAuditLogSample2() {
        return new AuditLog().id(2L).action("action2").targetEntity("targetEntity2").targetId("targetId2").ipAddress("ipAddress2");
    }

    public static AuditLog getAuditLogRandomSampleGenerator() {
        return new AuditLog()
            .id(longCount.incrementAndGet())
            .action(UUID.randomUUID().toString())
            .targetEntity(UUID.randomUUID().toString())
            .targetId(UUID.randomUUID().toString())
            .ipAddress(UUID.randomUUID().toString());
    }
}
