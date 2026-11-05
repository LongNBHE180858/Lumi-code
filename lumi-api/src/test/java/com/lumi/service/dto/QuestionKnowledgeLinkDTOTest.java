package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuestionKnowledgeLinkDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuestionKnowledgeLinkDTO.class);
        QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO1 = new QuestionKnowledgeLinkDTO();
        questionKnowledgeLinkDTO1.setId(1L);
        QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO2 = new QuestionKnowledgeLinkDTO();
        assertThat(questionKnowledgeLinkDTO1).isNotEqualTo(questionKnowledgeLinkDTO2);
        questionKnowledgeLinkDTO2.setId(questionKnowledgeLinkDTO1.getId());
        assertThat(questionKnowledgeLinkDTO1).isEqualTo(questionKnowledgeLinkDTO2);
        questionKnowledgeLinkDTO2.setId(2L);
        assertThat(questionKnowledgeLinkDTO1).isNotEqualTo(questionKnowledgeLinkDTO2);
        questionKnowledgeLinkDTO1.setId(null);
        assertThat(questionKnowledgeLinkDTO1).isNotEqualTo(questionKnowledgeLinkDTO2);
    }
}
