package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ParentStudentLinkTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static ParentStudentLink getParentStudentLinkSample1() {
        return new ParentStudentLink()
            .id(1L)
            .linkCode("linkCode1")
            .status("status1")
            .relationshipType("relationshipType1")
            .guardianVerificationStatus("guardianVerificationStatus1");
    }

    public static ParentStudentLink getParentStudentLinkSample2() {
        return new ParentStudentLink()
            .id(2L)
            .linkCode("linkCode2")
            .status("status2")
            .relationshipType("relationshipType2")
            .guardianVerificationStatus("guardianVerificationStatus2");
    }

    public static ParentStudentLink getParentStudentLinkRandomSampleGenerator() {
        return new ParentStudentLink()
            .id(longCount.incrementAndGet())
            .linkCode(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString())
            .relationshipType(UUID.randomUUID().toString())
            .guardianVerificationStatus(UUID.randomUUID().toString());
    }
}
