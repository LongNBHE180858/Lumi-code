package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InstructorWalletDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InstructorWalletDTO.class);
        InstructorWalletDTO instructorWalletDTO1 = new InstructorWalletDTO();
        instructorWalletDTO1.setId(1L);
        InstructorWalletDTO instructorWalletDTO2 = new InstructorWalletDTO();
        assertThat(instructorWalletDTO1).isNotEqualTo(instructorWalletDTO2);
        instructorWalletDTO2.setId(instructorWalletDTO1.getId());
        assertThat(instructorWalletDTO1).isEqualTo(instructorWalletDTO2);
        instructorWalletDTO2.setId(2L);
        assertThat(instructorWalletDTO1).isNotEqualTo(instructorWalletDTO2);
        instructorWalletDTO1.setId(null);
        assertThat(instructorWalletDTO1).isNotEqualTo(instructorWalletDTO2);
    }
}
