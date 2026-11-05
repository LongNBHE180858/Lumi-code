package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AiGeneratedContentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static AiGeneratedContent getAiGeneratedContentSample1() {
        return new AiGeneratedContent()
            .id(1L)
            .contentType("contentType1")
            .requestHash("requestHash1")
            .sourceVersion("sourceVersion1")
            .modelName("modelName1")
            .promptVersion("promptVersion1")
            .generationStatus("generationStatus1")
            .visibility("visibility1")
            .version(1L)
            .reviewStatus("reviewStatus1")
            .sourceQuestionId(1L)
            .contentHash("contentHash1");
    }

    public static AiGeneratedContent getAiGeneratedContentSample2() {
        return new AiGeneratedContent()
            .id(2L)
            .contentType("contentType2")
            .requestHash("requestHash2")
            .sourceVersion("sourceVersion2")
            .modelName("modelName2")
            .promptVersion("promptVersion2")
            .generationStatus("generationStatus2")
            .visibility("visibility2")
            .version(2L)
            .reviewStatus("reviewStatus2")
            .sourceQuestionId(2L)
            .contentHash("contentHash2");
    }

    public static AiGeneratedContent getAiGeneratedContentRandomSampleGenerator() {
        return new AiGeneratedContent()
            .id(longCount.incrementAndGet())
            .contentType(UUID.randomUUID().toString())
            .requestHash(UUID.randomUUID().toString())
            .sourceVersion(UUID.randomUUID().toString())
            .modelName(UUID.randomUUID().toString())
            .promptVersion(UUID.randomUUID().toString())
            .generationStatus(UUID.randomUUID().toString())
            .visibility(UUID.randomUUID().toString())
            .version(longCount.incrementAndGet())
            .reviewStatus(UUID.randomUUID().toString())
            .sourceQuestionId(longCount.incrementAndGet())
            .contentHash(UUID.randomUUID().toString());
    }
}
