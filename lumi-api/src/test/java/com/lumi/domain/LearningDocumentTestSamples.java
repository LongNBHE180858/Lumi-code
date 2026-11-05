package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LearningDocumentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static LearningDocument getLearningDocumentSample1() {
        return new LearningDocument()
            .id(1L)
            .sourceType("sourceType1")
            .title("title1")
            .fileObjectKey("fileObjectKey1")
            .contentHash("contentHash1")
            .sourceVersion(1)
            .processingStatus("processingStatus1")
            .indexStatus("indexStatus1")
            .extractedTextObjectKey("extractedTextObjectKey1")
            .embeddingModel("embeddingModel1")
            .bookSeries("bookSeries1");
    }

    public static LearningDocument getLearningDocumentSample2() {
        return new LearningDocument()
            .id(2L)
            .sourceType("sourceType2")
            .title("title2")
            .fileObjectKey("fileObjectKey2")
            .contentHash("contentHash2")
            .sourceVersion(2)
            .processingStatus("processingStatus2")
            .indexStatus("indexStatus2")
            .extractedTextObjectKey("extractedTextObjectKey2")
            .embeddingModel("embeddingModel2")
            .bookSeries("bookSeries2");
    }

    public static LearningDocument getLearningDocumentRandomSampleGenerator() {
        return new LearningDocument()
            .id(longCount.incrementAndGet())
            .sourceType(UUID.randomUUID().toString())
            .title(UUID.randomUUID().toString())
            .fileObjectKey(UUID.randomUUID().toString())
            .contentHash(UUID.randomUUID().toString())
            .sourceVersion(intCount.incrementAndGet())
            .processingStatus(UUID.randomUUID().toString())
            .indexStatus(UUID.randomUUID().toString())
            .extractedTextObjectKey(UUID.randomUUID().toString())
            .embeddingModel(UUID.randomUUID().toString())
            .bookSeries(UUID.randomUUID().toString());
    }
}
