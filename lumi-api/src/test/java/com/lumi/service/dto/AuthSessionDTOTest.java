package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuthSessionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuthSessionDTO.class);
        AuthSessionDTO authSessionDTO1 = new AuthSessionDTO();
        authSessionDTO1.setId(1L);
        AuthSessionDTO authSessionDTO2 = new AuthSessionDTO();
        assertThat(authSessionDTO1).isNotEqualTo(authSessionDTO2);
        authSessionDTO2.setId(authSessionDTO1.getId());
        assertThat(authSessionDTO1).isEqualTo(authSessionDTO2);
        authSessionDTO2.setId(2L);
        assertThat(authSessionDTO1).isNotEqualTo(authSessionDTO2);
        authSessionDTO1.setId(null);
        assertThat(authSessionDTO1).isNotEqualTo(authSessionDTO2);
    }
}
