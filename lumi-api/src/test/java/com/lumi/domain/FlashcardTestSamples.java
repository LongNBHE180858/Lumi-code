package com.lumi.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class FlashcardTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Flashcard getFlashcardSample1() {
        return new Flashcard().id(1L);
    }

    public static Flashcard getFlashcardSample2() {
        return new Flashcard().id(2L);
    }

    public static Flashcard getFlashcardRandomSampleGenerator() {
        return new Flashcard().id(longCount.incrementAndGet());
    }
}
