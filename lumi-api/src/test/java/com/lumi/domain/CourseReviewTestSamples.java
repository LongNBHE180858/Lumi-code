package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CourseReviewTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static CourseReview getCourseReviewSample1() {
        return new CourseReview().id(1L).rating(1).content("content1");
    }

    public static CourseReview getCourseReviewSample2() {
        return new CourseReview().id(2L).rating(2).content("content2");
    }

    public static CourseReview getCourseReviewRandomSampleGenerator() {
        return new CourseReview().id(longCount.incrementAndGet()).rating(intCount.incrementAndGet()).content(UUID.randomUUID().toString());
    }
}
