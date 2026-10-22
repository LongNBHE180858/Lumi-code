package com.lumi.domain;

import static com.lumi.domain.AuthSessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuthSessionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuthSession.class);
        AuthSession authSession1 = getAuthSessionSample1();
        AuthSession authSession2 = new AuthSession();
        assertThat(authSession1).isNotEqualTo(authSession2);

        authSession2.setId(authSession1.getId());
        assertThat(authSession1).isEqualTo(authSession2);

        authSession2 = getAuthSessionSample2();
        assertThat(authSession1).isNotEqualTo(authSession2);
    }
}
