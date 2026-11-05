package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ContentReportTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static ContentReport getContentReportSample1() {
        return new ContentReport().id(1L).targetType("targetType1").targetId(1L).status("status1").targetVersion("targetVersion1");
    }

    public static ContentReport getContentReportSample2() {
        return new ContentReport().id(2L).targetType("targetType2").targetId(2L).status("status2").targetVersion("targetVersion2");
    }

    public static ContentReport getContentReportRandomSampleGenerator() {
        return new ContentReport()
            .id(longCount.incrementAndGet())
            .targetType(UUID.randomUUID().toString())
            .targetId(longCount.incrementAndGet())
            .status(UUID.randomUUID().toString())
            .targetVersion(UUID.randomUUID().toString());
    }
}
