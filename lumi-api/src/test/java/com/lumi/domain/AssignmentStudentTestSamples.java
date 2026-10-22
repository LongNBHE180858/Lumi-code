package com.lumi.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class AssignmentStudentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static AssignmentStudent getAssignmentStudentSample1() {
        return new AssignmentStudent().id(1L);
    }

    public static AssignmentStudent getAssignmentStudentSample2() {
        return new AssignmentStudent().id(2L);
    }

    public static AssignmentStudent getAssignmentStudentRandomSampleGenerator() {
        return new AssignmentStudent().id(longCount.incrementAndGet());
    }
}
