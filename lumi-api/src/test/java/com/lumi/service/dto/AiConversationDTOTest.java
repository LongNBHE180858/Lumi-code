package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AiConversationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AiConversationDTO.class);
        AiConversationDTO aiConversationDTO1 = new AiConversationDTO();
        aiConversationDTO1.setId(1L);
        AiConversationDTO aiConversationDTO2 = new AiConversationDTO();
        assertThat(aiConversationDTO1).isNotEqualTo(aiConversationDTO2);
        aiConversationDTO2.setId(aiConversationDTO1.getId());
        assertThat(aiConversationDTO1).isEqualTo(aiConversationDTO2);
        aiConversationDTO2.setId(2L);
        assertThat(aiConversationDTO1).isNotEqualTo(aiConversationDTO2);
        aiConversationDTO1.setId(null);
        assertThat(aiConversationDTO1).isNotEqualTo(aiConversationDTO2);
    }
}
