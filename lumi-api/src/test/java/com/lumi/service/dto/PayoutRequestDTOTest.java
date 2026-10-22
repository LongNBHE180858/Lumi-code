package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PayoutRequestDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PayoutRequestDTO.class);
        PayoutRequestDTO payoutRequestDTO1 = new PayoutRequestDTO();
        payoutRequestDTO1.setId(1L);
        PayoutRequestDTO payoutRequestDTO2 = new PayoutRequestDTO();
        assertThat(payoutRequestDTO1).isNotEqualTo(payoutRequestDTO2);
        payoutRequestDTO2.setId(payoutRequestDTO1.getId());
        assertThat(payoutRequestDTO1).isEqualTo(payoutRequestDTO2);
        payoutRequestDTO2.setId(2L);
        assertThat(payoutRequestDTO1).isNotEqualTo(payoutRequestDTO2);
        payoutRequestDTO1.setId(null);
        assertThat(payoutRequestDTO1).isNotEqualTo(payoutRequestDTO2);
    }
}
