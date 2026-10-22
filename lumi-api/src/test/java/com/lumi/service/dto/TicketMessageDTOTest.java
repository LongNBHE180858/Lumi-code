package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TicketMessageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TicketMessageDTO.class);
        TicketMessageDTO ticketMessageDTO1 = new TicketMessageDTO();
        ticketMessageDTO1.setId(1L);
        TicketMessageDTO ticketMessageDTO2 = new TicketMessageDTO();
        assertThat(ticketMessageDTO1).isNotEqualTo(ticketMessageDTO2);
        ticketMessageDTO2.setId(ticketMessageDTO1.getId());
        assertThat(ticketMessageDTO1).isEqualTo(ticketMessageDTO2);
        ticketMessageDTO2.setId(2L);
        assertThat(ticketMessageDTO1).isNotEqualTo(ticketMessageDTO2);
        ticketMessageDTO1.setId(null);
        assertThat(ticketMessageDTO1).isNotEqualTo(ticketMessageDTO2);
    }
}
