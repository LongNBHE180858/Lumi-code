package com.lumi.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class StudentStreakTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static StudentStreak getStudentStreakSample1() {
        return new StudentStreak().id(1L).currentStreak(1).longestStreak(1).gemsBalance(1);
    }

    public static StudentStreak getStudentStreakSample2() {
        return new StudentStreak().id(2L).currentStreak(2).longestStreak(2).gemsBalance(2);
    }

    public static StudentStreak getStudentStreakRandomSampleGenerator() {
        return new StudentStreak()
            .id(longCount.incrementAndGet())
            .currentStreak(intCount.incrementAndGet())
            .longestStreak(intCount.incrementAndGet())
            .gemsBalance(intCount.incrementAndGet());
    }
}
