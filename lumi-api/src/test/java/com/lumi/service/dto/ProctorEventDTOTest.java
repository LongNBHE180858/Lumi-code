package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProctorEventDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProctorEventDTO.class);
        ProctorEventDTO proctorEventDTO1 = new ProctorEventDTO();
        proctorEventDTO1.setId(1L);
        ProctorEventDTO proctorEventDTO2 = new ProctorEventDTO();
        assertThat(proctorEventDTO1).isNotEqualTo(proctorEventDTO2);
        proctorEventDTO2.setId(proctorEventDTO1.getId());
        assertThat(proctorEventDTO1).isEqualTo(proctorEventDTO2);
        proctorEventDTO2.setId(2L);
        assertThat(proctorEventDTO1).isNotEqualTo(proctorEventDTO2);
        proctorEventDTO1.setId(null);
        assertThat(proctorEventDTO1).isNotEqualTo(proctorEventDTO2);
    }
}
