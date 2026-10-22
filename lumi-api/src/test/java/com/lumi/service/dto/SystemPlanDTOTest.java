package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SystemPlanDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SystemPlanDTO.class);
        SystemPlanDTO systemPlanDTO1 = new SystemPlanDTO();
        systemPlanDTO1.setId(1L);
        SystemPlanDTO systemPlanDTO2 = new SystemPlanDTO();
        assertThat(systemPlanDTO1).isNotEqualTo(systemPlanDTO2);
        systemPlanDTO2.setId(systemPlanDTO1.getId());
        assertThat(systemPlanDTO1).isEqualTo(systemPlanDTO2);
        systemPlanDTO2.setId(2L);
        assertThat(systemPlanDTO1).isNotEqualTo(systemPlanDTO2);
        systemPlanDTO1.setId(null);
        assertThat(systemPlanDTO1).isNotEqualTo(systemPlanDTO2);
    }
}
