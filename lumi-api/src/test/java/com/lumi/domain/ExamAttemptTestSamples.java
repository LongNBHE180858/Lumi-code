package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ExamAttemptTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static ExamAttempt getExamAttemptSample1() {
        return new ExamAttempt().id(1L).status("status1").rowVersion(1L);
    }

    public static ExamAttempt getExamAttemptSample2() {
        return new ExamAttempt().id(2L).status("status2").rowVersion(2L);
    }

    public static ExamAttempt getExamAttemptRandomSampleGenerator() {
        return new ExamAttempt()
            .id(longCount.incrementAndGet())
            .status(UUID.randomUUID().toString())
            .rowVersion(longCount.incrementAndGet());
    }
}
