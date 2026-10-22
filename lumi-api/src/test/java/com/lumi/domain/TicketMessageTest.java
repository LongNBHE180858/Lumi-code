package com.lumi.domain;

import static com.lumi.domain.SupportTicketTestSamples.*;
import static com.lumi.domain.TicketMessageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TicketMessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TicketMessage.class);
        TicketMessage ticketMessage1 = getTicketMessageSample1();
        TicketMessage ticketMessage2 = new TicketMessage();
        assertThat(ticketMessage1).isNotEqualTo(ticketMessage2);

        ticketMessage2.setId(ticketMessage1.getId());
        assertThat(ticketMessage1).isEqualTo(ticketMessage2);

        ticketMessage2 = getTicketMessageSample2();
        assertThat(ticketMessage1).isNotEqualTo(ticketMessage2);
    }

    @Test
    void ticketTest() {
        TicketMessage ticketMessage = getTicketMessageRandomSampleGenerator();
        SupportTicket supportTicketBack = getSupportTicketRandomSampleGenerator();

        ticketMessage.setTicket(supportTicketBack);
        assertThat(ticketMessage.getTicket()).isEqualTo(supportTicketBack);

        ticketMessage.ticket(null);
        assertThat(ticketMessage.getTicket()).isNull();
    }
}
