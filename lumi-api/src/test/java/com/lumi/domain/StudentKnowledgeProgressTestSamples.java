package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class StudentKnowledgeProgressTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static StudentKnowledgeProgress getStudentKnowledgeProgressSample1() {
        return new StudentKnowledgeProgress().id(1L).evidenceCount(1).calculationVersion("calculationVersion1");
    }

    public static StudentKnowledgeProgress getStudentKnowledgeProgressSample2() {
        return new StudentKnowledgeProgress().id(2L).evidenceCount(2).calculationVersion("calculationVersion2");
    }

    public static StudentKnowledgeProgress getStudentKnowledgeProgressRandomSampleGenerator() {
        return new StudentKnowledgeProgress()
            .id(longCount.incrementAndGet())
            .evidenceCount(intCount.incrementAndGet())
            .calculationVersion(UUID.randomUUID().toString());
    }
}
