package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AiGeneratedContentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AiGeneratedContentDTO.class);
        AiGeneratedContentDTO aiGeneratedContentDTO1 = new AiGeneratedContentDTO();
        aiGeneratedContentDTO1.setId(1L);
        AiGeneratedContentDTO aiGeneratedContentDTO2 = new AiGeneratedContentDTO();
        assertThat(aiGeneratedContentDTO1).isNotEqualTo(aiGeneratedContentDTO2);
        aiGeneratedContentDTO2.setId(aiGeneratedContentDTO1.getId());
        assertThat(aiGeneratedContentDTO1).isEqualTo(aiGeneratedContentDTO2);
        aiGeneratedContentDTO2.setId(2L);
        assertThat(aiGeneratedContentDTO1).isNotEqualTo(aiGeneratedContentDTO2);
        aiGeneratedContentDTO1.setId(null);
        assertThat(aiGeneratedContentDTO1).isNotEqualTo(aiGeneratedContentDTO2);
    }
}
