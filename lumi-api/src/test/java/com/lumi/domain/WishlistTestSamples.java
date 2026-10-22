package com.lumi.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class WishlistTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Wishlist getWishlistSample1() {
        return new Wishlist().id(1L);
    }

    public static Wishlist getWishlistSample2() {
        return new Wishlist().id(2L);
    }

    public static Wishlist getWishlistRandomSampleGenerator() {
        return new Wishlist().id(longCount.incrementAndGet());
    }
}
