package com.lumi.domain;

import static com.lumi.domain.ExamAttemptTestSamples.*;
import static com.lumi.domain.ProctorSessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProctorSessionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProctorSession.class);
        ProctorSession proctorSession1 = getProctorSessionSample1();
        ProctorSession proctorSession2 = new ProctorSession();
        assertThat(proctorSession1).isNotEqualTo(proctorSession2);

        proctorSession2.setId(proctorSession1.getId());
        assertThat(proctorSession1).isEqualTo(proctorSession2);

        proctorSession2 = getProctorSessionSample2();
        assertThat(proctorSession1).isNotEqualTo(proctorSession2);
    }

    @Test
    void attemptTest() {
        ProctorSession proctorSession = getProctorSessionRandomSampleGenerator();
        ExamAttempt examAttemptBack = getExamAttemptRandomSampleGenerator();

        proctorSession.setAttempt(examAttemptBack);
        assertThat(proctorSession.getAttempt()).isEqualTo(examAttemptBack);

        proctorSession.attempt(null);
        assertThat(proctorSession.getAttempt()).isNull();
    }
}
