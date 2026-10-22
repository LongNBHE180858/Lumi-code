package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AssignmentSubmissionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static AssignmentSubmission getAssignmentSubmissionSample1() {
        return new AssignmentSubmission().id(1L).content("content1").teacherFeedback("teacherFeedback1").status("status1");
    }

    public static AssignmentSubmission getAssignmentSubmissionSample2() {
        return new AssignmentSubmission().id(2L).content("content2").teacherFeedback("teacherFeedback2").status("status2");
    }

    public static AssignmentSubmission getAssignmentSubmissionRandomSampleGenerator() {
        return new AssignmentSubmission()
            .id(longCount.incrementAndGet())
            .content(UUID.randomUUID().toString())
            .teacherFeedback(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString());
    }
}
