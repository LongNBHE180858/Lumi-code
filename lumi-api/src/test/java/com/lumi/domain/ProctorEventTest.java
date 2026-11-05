package com.lumi.domain;

import static com.lumi.domain.ExamAttemptTestSamples.*;
import static com.lumi.domain.ProctorEventTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProctorEventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProctorEvent.class);
        ProctorEvent proctorEvent1 = getProctorEventSample1();
        ProctorEvent proctorEvent2 = new ProctorEvent();
        assertThat(proctorEvent1).isNotEqualTo(proctorEvent2);

        proctorEvent2.setId(proctorEvent1.getId());
        assertThat(proctorEvent1).isEqualTo(proctorEvent2);

        proctorEvent2 = getProctorEventSample2();
        assertThat(proctorEvent1).isNotEqualTo(proctorEvent2);
    }

    @Test
    void attemptTest() {
        ProctorEvent proctorEvent = getProctorEventRandomSampleGenerator();
        ExamAttempt examAttemptBack = getExamAttemptRandomSampleGenerator();

        proctorEvent.setAttempt(examAttemptBack);
        assertThat(proctorEvent.getAttempt()).isEqualTo(examAttemptBack);

        proctorEvent.attempt(null);
        assertThat(proctorEvent.getAttempt()).isNull();
    }
}
