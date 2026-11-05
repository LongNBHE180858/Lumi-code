package com.lumi.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FlashcardReviewTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static FlashcardReview getFlashcardReviewSample1() {
        return new FlashcardReview().id(1L).lastRating(1).reviewCount(1);
    }

    public static FlashcardReview getFlashcardReviewSample2() {
        return new FlashcardReview().id(2L).lastRating(2).reviewCount(2);
    }

    public static FlashcardReview getFlashcardReviewRandomSampleGenerator() {
        return new FlashcardReview()
            .id(longCount.incrementAndGet())
            .lastRating(intCount.incrementAndGet())
            .reviewCount(intCount.incrementAndGet());
    }
}
