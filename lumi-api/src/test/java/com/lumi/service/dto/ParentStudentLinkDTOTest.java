package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ParentStudentLinkDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParentStudentLinkDTO.class);
        ParentStudentLinkDTO parentStudentLinkDTO1 = new ParentStudentLinkDTO();
        parentStudentLinkDTO1.setId(1L);
        ParentStudentLinkDTO parentStudentLinkDTO2 = new ParentStudentLinkDTO();
        assertThat(parentStudentLinkDTO1).isNotEqualTo(parentStudentLinkDTO2);
        parentStudentLinkDTO2.setId(parentStudentLinkDTO1.getId());
        assertThat(parentStudentLinkDTO1).isEqualTo(parentStudentLinkDTO2);
        parentStudentLinkDTO2.setId(2L);
        assertThat(parentStudentLinkDTO1).isNotEqualTo(parentStudentLinkDTO2);
        parentStudentLinkDTO1.setId(null);
        assertThat(parentStudentLinkDTO1).isNotEqualTo(parentStudentLinkDTO2);
    }
}
