package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TeacherVerificationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static TeacherVerification getTeacherVerificationSample1() {
        return new TeacherVerification()
            .id(1L)
            .identityDocumentUrl("identityDocumentUrl1")
            .verificationStatus("verificationStatus1")
            .notes("notes1");
    }

    public static TeacherVerification getTeacherVerificationSample2() {
        return new TeacherVerification()
            .id(2L)
            .identityDocumentUrl("identityDocumentUrl2")
            .verificationStatus("verificationStatus2")
            .notes("notes2");
    }

    public static TeacherVerification getTeacherVerificationRandomSampleGenerator() {
        return new TeacherVerification()
            .id(longCount.incrementAndGet())
            .identityDocumentUrl(UUID.randomUUID().toString())
            .verificationStatus(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString());
    }
}
