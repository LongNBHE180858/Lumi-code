package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CourseReviewRequestTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static CourseReviewRequest getCourseReviewRequestSample1() {
        return new CourseReviewRequest()
            .id(1L)
            .courseVersion(1L)
            .status(com.lumi.domain.enumeration.CourseReviewRequestStatus.PENDING)
            .submissionNote("submissionNote1")
            .reviewerFeedback("reviewerFeedback1");
    }

    public static CourseReviewRequest getCourseReviewRequestSample2() {
        return new CourseReviewRequest()
            .id(2L)
            .courseVersion(2L)
            .status(com.lumi.domain.enumeration.CourseReviewRequestStatus.PENDING)
            .submissionNote("submissionNote2")
            .reviewerFeedback("reviewerFeedback2");
    }

    public static CourseReviewRequest getCourseReviewRequestRandomSampleGenerator() {
        return new CourseReviewRequest()
            .id(longCount.incrementAndGet())
            .courseVersion(longCount.incrementAndGet())
            .status(com.lumi.domain.enumeration.CourseReviewRequestStatus.PENDING)
            .submissionNote(UUID.randomUUID().toString())
            .reviewerFeedback(UUID.randomUUID().toString());
    }
}
