package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClassMaterialDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClassMaterialDTO.class);
        ClassMaterialDTO classMaterialDTO1 = new ClassMaterialDTO();
        classMaterialDTO1.setId(1L);
        ClassMaterialDTO classMaterialDTO2 = new ClassMaterialDTO();
        assertThat(classMaterialDTO1).isNotEqualTo(classMaterialDTO2);
        classMaterialDTO2.setId(classMaterialDTO1.getId());
        assertThat(classMaterialDTO1).isEqualTo(classMaterialDTO2);
        classMaterialDTO2.setId(2L);
        assertThat(classMaterialDTO1).isNotEqualTo(classMaterialDTO2);
        classMaterialDTO1.setId(null);
        assertThat(classMaterialDTO1).isNotEqualTo(classMaterialDTO2);
    }
}
