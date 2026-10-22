package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SupportTicketDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SupportTicketDTO.class);
        SupportTicketDTO supportTicketDTO1 = new SupportTicketDTO();
        supportTicketDTO1.setId(1L);
        SupportTicketDTO supportTicketDTO2 = new SupportTicketDTO();
        assertThat(supportTicketDTO1).isNotEqualTo(supportTicketDTO2);
        supportTicketDTO2.setId(supportTicketDTO1.getId());
        assertThat(supportTicketDTO1).isEqualTo(supportTicketDTO2);
        supportTicketDTO2.setId(2L);
        assertThat(supportTicketDTO1).isNotEqualTo(supportTicketDTO2);
        supportTicketDTO1.setId(null);
        assertThat(supportTicketDTO1).isNotEqualTo(supportTicketDTO2);
    }
}
