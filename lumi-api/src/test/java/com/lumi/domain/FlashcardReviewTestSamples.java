package com.lumi.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class FlashcardReviewTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static FlashcardReview getFlashcardReviewSample1() {
        return new FlashcardReview().id(1L);
    }

    public static FlashcardReview getFlashcardReviewSample2() {
        return new FlashcardReview().id(2L);
    }

    public static FlashcardReview getFlashcardReviewRandomSampleGenerator() {
        return new FlashcardReview().id(longCount.incrementAndGet());
    }
}
