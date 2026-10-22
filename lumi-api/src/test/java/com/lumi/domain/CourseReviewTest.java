package com.lumi.domain;

import static com.lumi.domain.CourseReviewTestSamples.*;
import static com.lumi.domain.CourseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CourseReviewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseReview.class);
        CourseReview courseReview1 = getCourseReviewSample1();
        CourseReview courseReview2 = new CourseReview();
        assertThat(courseReview1).isNotEqualTo(courseReview2);

        courseReview2.setId(courseReview1.getId());
        assertThat(courseReview1).isEqualTo(courseReview2);

        courseReview2 = getCourseReviewSample2();
        assertThat(courseReview1).isNotEqualTo(courseReview2);
    }

    @Test
    void courseTest() {
        CourseReview courseReview = getCourseReviewRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        courseReview.setCourse(courseBack);
        assertThat(courseReview.getCourse()).isEqualTo(courseBack);

        courseReview.course(null);
        assertThat(courseReview.getCourse()).isNull();
    }
}
