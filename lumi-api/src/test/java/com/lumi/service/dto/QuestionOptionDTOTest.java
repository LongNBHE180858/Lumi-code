package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuestionOptionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuestionOptionDTO.class);
        QuestionOptionDTO questionOptionDTO1 = new QuestionOptionDTO();
        questionOptionDTO1.setId(1L);
        QuestionOptionDTO questionOptionDTO2 = new QuestionOptionDTO();
        assertThat(questionOptionDTO1).isNotEqualTo(questionOptionDTO2);
        questionOptionDTO2.setId(questionOptionDTO1.getId());
        assertThat(questionOptionDTO1).isEqualTo(questionOptionDTO2);
        questionOptionDTO2.setId(2L);
        assertThat(questionOptionDTO1).isNotEqualTo(questionOptionDTO2);
        questionOptionDTO1.setId(null);
        assertThat(questionOptionDTO1).isNotEqualTo(questionOptionDTO2);
    }
}
