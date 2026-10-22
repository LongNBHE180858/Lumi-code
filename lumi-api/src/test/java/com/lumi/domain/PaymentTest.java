package com.lumi.domain;

import static com.lumi.domain.CourseOrderTestSamples.*;
import static com.lumi.domain.PaymentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaymentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Payment.class);
        Payment payment1 = getPaymentSample1();
        Payment payment2 = new Payment();
        assertThat(payment1).isNotEqualTo(payment2);

        payment2.setId(payment1.getId());
        assertThat(payment1).isEqualTo(payment2);

        payment2 = getPaymentSample2();
        assertThat(payment1).isNotEqualTo(payment2);
    }

    @Test
    void orderTest() {
        Payment payment = getPaymentRandomSampleGenerator();
        CourseOrder courseOrderBack = getCourseOrderRandomSampleGenerator();

        payment.setOrder(courseOrderBack);
        assertThat(payment.getOrder()).isEqualTo(courseOrderBack);

        payment.order(null);
        assertThat(payment.getOrder()).isNull();
    }
}
