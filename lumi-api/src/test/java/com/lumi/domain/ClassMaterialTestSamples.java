package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ClassMaterialTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static ClassMaterial getClassMaterialSample1() {
        return new ClassMaterial()
            .id(1L)
            .title("title1")
            .fileType("fileType1")
            .fileUrl("fileUrl1")
            .fileSizeBytes(1L)
            .fileObjectKey("fileObjectKey1");
    }

    public static ClassMaterial getClassMaterialSample2() {
        return new ClassMaterial()
            .id(2L)
            .title("title2")
            .fileType("fileType2")
            .fileUrl("fileUrl2")
            .fileSizeBytes(2L)
            .fileObjectKey("fileObjectKey2");
    }

    public static ClassMaterial getClassMaterialRandomSampleGenerator() {
        return new ClassMaterial()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .fileType(UUID.randomUUID().toString())
            .fileUrl(UUID.randomUUID().toString())
            .fileSizeBytes(longCount.incrementAndGet())
            .fileObjectKey(UUID.randomUUID().toString());
    }
}
