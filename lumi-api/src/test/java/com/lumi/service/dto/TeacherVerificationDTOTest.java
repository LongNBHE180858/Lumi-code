package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TeacherVerificationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeacherVerificationDTO.class);
        TeacherVerificationDTO teacherVerificationDTO1 = new TeacherVerificationDTO();
        teacherVerificationDTO1.setId(1L);
        TeacherVerificationDTO teacherVerificationDTO2 = new TeacherVerificationDTO();
        assertThat(teacherVerificationDTO1).isNotEqualTo(teacherVerificationDTO2);
        teacherVerificationDTO2.setId(teacherVerificationDTO1.getId());
        assertThat(teacherVerificationDTO1).isEqualTo(teacherVerificationDTO2);
        teacherVerificationDTO2.setId(2L);
        assertThat(teacherVerificationDTO1).isNotEqualTo(teacherVerificationDTO2);
        teacherVerificationDTO1.setId(null);
        assertThat(teacherVerificationDTO1).isNotEqualTo(teacherVerificationDTO2);
    }
}
