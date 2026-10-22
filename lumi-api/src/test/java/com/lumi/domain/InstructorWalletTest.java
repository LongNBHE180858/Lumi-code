package com.lumi.domain;

import static com.lumi.domain.InstructorWalletTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InstructorWalletTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InstructorWallet.class);
        InstructorWallet instructorWallet1 = getInstructorWalletSample1();
        InstructorWallet instructorWallet2 = new InstructorWallet();
        assertThat(instructorWallet1).isNotEqualTo(instructorWallet2);

        instructorWallet2.setId(instructorWallet1.getId());
        assertThat(instructorWallet1).isEqualTo(instructorWallet2);

        instructorWallet2 = getInstructorWalletSample2();
        assertThat(instructorWallet1).isNotEqualTo(instructorWallet2);
    }
}
