package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CourseMaterialTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static CourseMaterial getCourseMaterialSample1() {
        return new CourseMaterial()
            .id(1L)
            .fileName("fileName1")
            .displayName("displayName1")
            .mimeType("mimeType1")
            .orderIndex(1)
            .fileType("fileType1")
            .fileUrl("fileUrl1")
            .fileSizeBytes(1L)
            .fileObjectKey("fileObjectKey1");
    }

    public static CourseMaterial getCourseMaterialSample2() {
        return new CourseMaterial()
            .id(2L)
            .fileName("fileName2")
            .displayName("displayName2")
            .mimeType("mimeType2")
            .orderIndex(2)
            .fileType("fileType2")
            .fileUrl("fileUrl2")
            .fileSizeBytes(2L)
            .fileObjectKey("fileObjectKey2");
    }

    public static CourseMaterial getCourseMaterialRandomSampleGenerator() {
        return new CourseMaterial()
            .id(longCount.incrementAndGet())
            .fileName(UUID.randomUUID().toString())
            .displayName(UUID.randomUUID().toString())
            .mimeType(UUID.randomUUID().toString())
            .orderIndex(intCount.incrementAndGet())
            .fileType(UUID.randomUUID().toString())
            .fileUrl(UUID.randomUUID().toString())
            .fileSizeBytes(longCount.incrementAndGet())
            .fileObjectKey(UUID.randomUUID().toString());
    }
}
