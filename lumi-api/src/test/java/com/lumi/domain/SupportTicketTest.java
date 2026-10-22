package com.lumi.domain;

import static com.lumi.domain.SupportTicketTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SupportTicketTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SupportTicket.class);
        SupportTicket supportTicket1 = getSupportTicketSample1();
        SupportTicket supportTicket2 = new SupportTicket();
        assertThat(supportTicket1).isNotEqualTo(supportTicket2);

        supportTicket2.setId(supportTicket1.getId());
        assertThat(supportTicket1).isEqualTo(supportTicket2);

        supportTicket2 = getSupportTicketSample2();
        assertThat(supportTicket1).isNotEqualTo(supportTicket2);
    }
}
