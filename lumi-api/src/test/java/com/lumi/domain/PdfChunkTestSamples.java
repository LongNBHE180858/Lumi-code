package com.lumi.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PdfChunkTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static PdfChunk getPdfChunkSample1() {
        return new PdfChunk().id(1L).pageNumber(1).chunkIndex(1).indexVersion(1);
    }

    public static PdfChunk getPdfChunkSample2() {
        return new PdfChunk().id(2L).pageNumber(2).chunkIndex(2).indexVersion(2);
    }

    public static PdfChunk getPdfChunkRandomSampleGenerator() {
        return new PdfChunk()
            .id(longCount.incrementAndGet())
            .pageNumber(intCount.incrementAndGet())
            .chunkIndex(intCount.incrementAndGet())
            .indexVersion(intCount.incrementAndGet());
    }
}
