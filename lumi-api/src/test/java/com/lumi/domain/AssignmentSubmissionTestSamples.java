package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AssignmentSubmissionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static AssignmentSubmission getAssignmentSubmissionSample1() {
        return new AssignmentSubmission().id(1L).status("status1").rowVersion(1L);
    }

    public static AssignmentSubmission getAssignmentSubmissionSample2() {
        return new AssignmentSubmission().id(2L).status("status2").rowVersion(2L);
    }

    public static AssignmentSubmission getAssignmentSubmissionRandomSampleGenerator() {
        return new AssignmentSubmission()
            .id(longCount.incrementAndGet())
            .status(UUID.randomUUID().toString())
            .rowVersion(longCount.incrementAndGet());
    }
}
