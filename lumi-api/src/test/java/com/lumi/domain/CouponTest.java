package com.lumi.domain;

import static com.lumi.domain.CouponTestSamples.*;
import static com.lumi.domain.CourseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CouponTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Coupon.class);
        Coupon coupon1 = getCouponSample1();
        Coupon coupon2 = new Coupon();
        assertThat(coupon1).isNotEqualTo(coupon2);

        coupon2.setId(coupon1.getId());
        assertThat(coupon1).isEqualTo(coupon2);

        coupon2 = getCouponSample2();
        assertThat(coupon1).isNotEqualTo(coupon2);
    }

    @Test
    void courseTest() {
        Coupon coupon = getCouponRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        coupon.setCourse(courseBack);
        assertThat(coupon.getCourse()).isEqualTo(courseBack);

        coupon.course(null);
        assertThat(coupon.getCourse()).isNull();
    }
}
