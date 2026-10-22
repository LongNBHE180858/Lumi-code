package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CourseMaterialDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseMaterialDTO.class);
        CourseMaterialDTO courseMaterialDTO1 = new CourseMaterialDTO();
        courseMaterialDTO1.setId(1L);
        CourseMaterialDTO courseMaterialDTO2 = new CourseMaterialDTO();
        assertThat(courseMaterialDTO1).isNotEqualTo(courseMaterialDTO2);
        courseMaterialDTO2.setId(courseMaterialDTO1.getId());
        assertThat(courseMaterialDTO1).isEqualTo(courseMaterialDTO2);
        courseMaterialDTO2.setId(2L);
        assertThat(courseMaterialDTO1).isNotEqualTo(courseMaterialDTO2);
        courseMaterialDTO1.setId(null);
        assertThat(courseMaterialDTO1).isNotEqualTo(courseMaterialDTO2);
    }
}
