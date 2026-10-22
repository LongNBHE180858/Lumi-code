package com.lumi.domain;

import static com.lumi.domain.ProctorEventTestSamples.*;
import static com.lumi.domain.ProctorSessionTestSamples.*;
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
    void sessionTest() {
        ProctorEvent proctorEvent = getProctorEventRandomSampleGenerator();
        ProctorSession proctorSessionBack = getProctorSessionRandomSampleGenerator();

        proctorEvent.setSession(proctorSessionBack);
        assertThat(proctorEvent.getSession()).isEqualTo(proctorSessionBack);

        proctorEvent.session(null);
        assertThat(proctorEvent.getSession()).isNull();
    }
}
