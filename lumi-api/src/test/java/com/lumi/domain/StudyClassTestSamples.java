package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class StudyClassTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static StudyClass getStudyClassSample1() {
        return new StudyClass().id(1L).name("name1").joinCode("joinCode1").maxStudents(1).currentStudents(1);
    }

    public static StudyClass getStudyClassSample2() {
        return new StudyClass().id(2L).name("name2").joinCode("joinCode2").maxStudents(2).currentStudents(2);
    }

    public static StudyClass getStudyClassRandomSampleGenerator() {
        return new StudyClass()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .joinCode(UUID.randomUUID().toString())
            .maxStudents(intCount.incrementAndGet())
            .currentStudents(intCount.incrementAndGet());
    }
}
