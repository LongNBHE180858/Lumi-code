package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClassMemberDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClassMemberDTO.class);
        ClassMemberDTO classMemberDTO1 = new ClassMemberDTO();
        classMemberDTO1.setId(1L);
        ClassMemberDTO classMemberDTO2 = new ClassMemberDTO();
        assertThat(classMemberDTO1).isNotEqualTo(classMemberDTO2);
        classMemberDTO2.setId(classMemberDTO1.getId());
        assertThat(classMemberDTO1).isEqualTo(classMemberDTO2);
        classMemberDTO2.setId(2L);
        assertThat(classMemberDTO1).isNotEqualTo(classMemberDTO2);
        classMemberDTO1.setId(null);
        assertThat(classMemberDTO1).isNotEqualTo(classMemberDTO2);
    }
}
