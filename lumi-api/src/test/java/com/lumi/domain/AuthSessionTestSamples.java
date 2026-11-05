package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AuthSessionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static AuthSession getAuthSessionSample1() {
        return new AuthSession().id(1L).deviceInfo("deviceInfo1").refreshToken("refreshToken1").refreshTokenHash("refreshTokenHash1");
    }

    public static AuthSession getAuthSessionSample2() {
        return new AuthSession().id(2L).deviceInfo("deviceInfo2").refreshToken("refreshToken2").refreshTokenHash("refreshTokenHash2");
    }

    public static AuthSession getAuthSessionRandomSampleGenerator() {
        return new AuthSession()
            .id(longCount.incrementAndGet())
            .deviceInfo(UUID.randomUUID().toString())
            .refreshToken(UUID.randomUUID().toString())
            .refreshTokenHash(UUID.randomUUID().toString());
    }
}
