package com.lumi.domain;

import static com.lumi.domain.InstructorWalletTestSamples.*;
import static com.lumi.domain.PayoutRequestTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PayoutRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PayoutRequest.class);
        PayoutRequest payoutRequest1 = getPayoutRequestSample1();
        PayoutRequest payoutRequest2 = new PayoutRequest();
        assertThat(payoutRequest1).isNotEqualTo(payoutRequest2);

        payoutRequest2.setId(payoutRequest1.getId());
        assertThat(payoutRequest1).isEqualTo(payoutRequest2);

        payoutRequest2 = getPayoutRequestSample2();
        assertThat(payoutRequest1).isNotEqualTo(payoutRequest2);
    }

    @Test
    void walletTest() {
        PayoutRequest payoutRequest = getPayoutRequestRandomSampleGenerator();
        InstructorWallet instructorWalletBack = getInstructorWalletRandomSampleGenerator();

        payoutRequest.setWallet(instructorWalletBack);
        assertThat(payoutRequest.getWallet()).isEqualTo(instructorWalletBack);

        payoutRequest.wallet(null);
        assertThat(payoutRequest.getWallet()).isNull();
    }
}
