package com.lumi.domain;

import static com.lumi.domain.CourseReviewRequestTestSamples.*;
import static com.lumi.domain.CourseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CourseReviewRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseReviewRequest.class);
        CourseReviewRequest courseReviewRequest1 = getCourseReviewRequestSample1();
        CourseReviewRequest courseReviewRequest2 = new CourseReviewRequest();
        assertThat(courseReviewRequest1).isNotEqualTo(courseReviewRequest2);

        courseReviewRequest2.setId(courseReviewRequest1.getId());
        assertThat(courseReviewRequest1).isEqualTo(courseReviewRequest2);

        courseReviewRequest2 = getCourseReviewRequestSample2();
        assertThat(courseReviewRequest1).isNotEqualTo(courseReviewRequest2);
    }

    @Test
    void courseTest() {
        CourseReviewRequest courseReviewRequest = getCourseReviewRequestRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        courseReviewRequest.setCourse(courseBack);
        assertThat(courseReviewRequest.getCourse()).isEqualTo(courseBack);

        courseReviewRequest.course(null);
        assertThat(courseReviewRequest.getCourse()).isNull();
    }
}
