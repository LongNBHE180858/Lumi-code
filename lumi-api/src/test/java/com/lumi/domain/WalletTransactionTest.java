package com.lumi.domain;

import static com.lumi.domain.CourseOrderTestSamples.*;
import static com.lumi.domain.InstructorWalletTestSamples.*;
import static com.lumi.domain.PayoutRequestTestSamples.*;
import static com.lumi.domain.RefundTestSamples.*;
import static com.lumi.domain.WalletTransactionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WalletTransactionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WalletTransaction.class);
        WalletTransaction walletTransaction1 = getWalletTransactionSample1();
        WalletTransaction walletTransaction2 = new WalletTransaction();
        assertThat(walletTransaction1).isNotEqualTo(walletTransaction2);

        walletTransaction2.setId(walletTransaction1.getId());
        assertThat(walletTransaction1).isEqualTo(walletTransaction2);

        walletTransaction2 = getWalletTransactionSample2();
        assertThat(walletTransaction1).isNotEqualTo(walletTransaction2);
    }

    @Test
    void walletTest() {
        WalletTransaction walletTransaction = getWalletTransactionRandomSampleGenerator();
        InstructorWallet instructorWalletBack = getInstructorWalletRandomSampleGenerator();

        walletTransaction.setWallet(instructorWalletBack);
        assertThat(walletTransaction.getWallet()).isEqualTo(instructorWalletBack);

        walletTransaction.wallet(null);
        assertThat(walletTransaction.getWallet()).isNull();
    }

    @Test
    void orderTest() {
        WalletTransaction walletTransaction = getWalletTransactionRandomSampleGenerator();
        CourseOrder courseOrderBack = getCourseOrderRandomSampleGenerator();

        walletTransaction.setOrder(courseOrderBack);
        assertThat(walletTransaction.getOrder()).isEqualTo(courseOrderBack);

        walletTransaction.order(null);
        assertThat(walletTransaction.getOrder()).isNull();
    }

    @Test
    void payoutRequestTest() {
        WalletTransaction walletTransaction = getWalletTransactionRandomSampleGenerator();
        PayoutRequest payoutRequestBack = getPayoutRequestRandomSampleGenerator();

        walletTransaction.setPayoutRequest(payoutRequestBack);
        assertThat(walletTransaction.getPayoutRequest()).isEqualTo(payoutRequestBack);

        walletTransaction.payoutRequest(null);
        assertThat(walletTransaction.getPayoutRequest()).isNull();
    }

    @Test
    void refundTest() {
        WalletTransaction walletTransaction = getWalletTransactionRandomSampleGenerator();
        Refund refundBack = getRefundRandomSampleGenerator();

        walletTransaction.setRefund(refundBack);
        assertThat(walletTransaction.getRefund()).isEqualTo(refundBack);

        walletTransaction.refund(null);
        assertThat(walletTransaction.getRefund()).isNull();
    }
}
