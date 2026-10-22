package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProctorSessionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProctorSessionDTO.class);
        ProctorSessionDTO proctorSessionDTO1 = new ProctorSessionDTO();
        proctorSessionDTO1.setId(1L);
        ProctorSessionDTO proctorSessionDTO2 = new ProctorSessionDTO();
        assertThat(proctorSessionDTO1).isNotEqualTo(proctorSessionDTO2);
        proctorSessionDTO2.setId(proctorSessionDTO1.getId());
        assertThat(proctorSessionDTO1).isEqualTo(proctorSessionDTO2);
        proctorSessionDTO2.setId(2L);
        assertThat(proctorSessionDTO1).isNotEqualTo(proctorSessionDTO2);
        proctorSessionDTO1.setId(null);
        assertThat(proctorSessionDTO1).isNotEqualTo(proctorSessionDTO2);
    }
}
