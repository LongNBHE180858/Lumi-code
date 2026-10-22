package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CourseMaterialTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static CourseMaterial getCourseMaterialSample1() {
        return new CourseMaterial().id(1L).fileType("fileType1").fileUrl("fileUrl1").fileSizeBytes(1L);
    }

    public static CourseMaterial getCourseMaterialSample2() {
        return new CourseMaterial().id(2L).fileType("fileType2").fileUrl("fileUrl2").fileSizeBytes(2L);
    }

    public static CourseMaterial getCourseMaterialRandomSampleGenerator() {
        return new CourseMaterial()
            .id(longCount.incrementAndGet())
            .fileType(UUID.randomUUID().toString())
            .fileUrl(UUID.randomUUID().toString())
            .fileSizeBytes(longCount.incrementAndGet());
    }
}
