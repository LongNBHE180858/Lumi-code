package com.lumi.domain;

import static com.lumi.domain.CouponTestSamples.*;
import static com.lumi.domain.CourseOrderTestSamples.*;
import static com.lumi.domain.CourseTestSamples.*;
import static com.lumi.domain.SystemPlanTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CourseOrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseOrder.class);
        CourseOrder courseOrder1 = getCourseOrderSample1();
        CourseOrder courseOrder2 = new CourseOrder();
        assertThat(courseOrder1).isNotEqualTo(courseOrder2);

        courseOrder2.setId(courseOrder1.getId());
        assertThat(courseOrder1).isEqualTo(courseOrder2);

        courseOrder2 = getCourseOrderSample2();
        assertThat(courseOrder1).isNotEqualTo(courseOrder2);
    }

    @Test
    void courseTest() {
        CourseOrder courseOrder = getCourseOrderRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        courseOrder.setCourse(courseBack);
        assertThat(courseOrder.getCourse()).isEqualTo(courseBack);

        courseOrder.course(null);
        assertThat(courseOrder.getCourse()).isNull();
    }

    @Test
    void planTest() {
        CourseOrder courseOrder = getCourseOrderRandomSampleGenerator();
        SystemPlan systemPlanBack = getSystemPlanRandomSampleGenerator();

        courseOrder.setPlan(systemPlanBack);
        assertThat(courseOrder.getPlan()).isEqualTo(systemPlanBack);

        courseOrder.plan(null);
        assertThat(courseOrder.getPlan()).isNull();
    }

    @Test
    void couponTest() {
        CourseOrder courseOrder = getCourseOrderRandomSampleGenerator();
        Coupon couponBack = getCouponRandomSampleGenerator();

        courseOrder.setCoupon(couponBack);
        assertThat(courseOrder.getCoupon()).isEqualTo(couponBack);

        courseOrder.coupon(null);
        assertThat(courseOrder.getCoupon()).isNull();
    }
}
