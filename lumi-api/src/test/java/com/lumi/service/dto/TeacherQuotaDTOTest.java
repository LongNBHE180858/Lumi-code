package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TeacherQuotaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeacherQuotaDTO.class);
        TeacherQuotaDTO teacherQuotaDTO1 = new TeacherQuotaDTO();
        teacherQuotaDTO1.setId(1L);
        TeacherQuotaDTO teacherQuotaDTO2 = new TeacherQuotaDTO();
        assertThat(teacherQuotaDTO1).isNotEqualTo(teacherQuotaDTO2);
        teacherQuotaDTO2.setId(teacherQuotaDTO1.getId());
        assertThat(teacherQuotaDTO1).isEqualTo(teacherQuotaDTO2);
        teacherQuotaDTO2.setId(2L);
        assertThat(teacherQuotaDTO1).isNotEqualTo(teacherQuotaDTO2);
        teacherQuotaDTO1.setId(null);
        assertThat(teacherQuotaDTO1).isNotEqualTo(teacherQuotaDTO2);
    }
}
