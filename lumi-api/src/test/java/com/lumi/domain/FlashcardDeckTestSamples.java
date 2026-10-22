package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FlashcardDeckTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static FlashcardDeck getFlashcardDeckSample1() {
        return new FlashcardDeck().id(1L).title("title1");
    }

    public static FlashcardDeck getFlashcardDeckSample2() {
        return new FlashcardDeck().id(2L).title("title2");
    }

    public static FlashcardDeck getFlashcardDeckRandomSampleGenerator() {
        return new FlashcardDeck().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString());
    }
}
