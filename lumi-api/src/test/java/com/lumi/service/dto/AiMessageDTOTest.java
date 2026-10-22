package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AiMessageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AiMessageDTO.class);
        AiMessageDTO aiMessageDTO1 = new AiMessageDTO();
        aiMessageDTO1.setId(1L);
        AiMessageDTO aiMessageDTO2 = new AiMessageDTO();
        assertThat(aiMessageDTO1).isNotEqualTo(aiMessageDTO2);
        aiMessageDTO2.setId(aiMessageDTO1.getId());
        assertThat(aiMessageDTO1).isEqualTo(aiMessageDTO2);
        aiMessageDTO2.setId(2L);
        assertThat(aiMessageDTO1).isNotEqualTo(aiMessageDTO2);
        aiMessageDTO1.setId(null);
        assertThat(aiMessageDTO1).isNotEqualTo(aiMessageDTO2);
    }
}
