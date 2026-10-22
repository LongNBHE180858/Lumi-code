package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuestionBankDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuestionBankDTO.class);
        QuestionBankDTO questionBankDTO1 = new QuestionBankDTO();
        questionBankDTO1.setId(1L);
        QuestionBankDTO questionBankDTO2 = new QuestionBankDTO();
        assertThat(questionBankDTO1).isNotEqualTo(questionBankDTO2);
        questionBankDTO2.setId(questionBankDTO1.getId());
        assertThat(questionBankDTO1).isEqualTo(questionBankDTO2);
        questionBankDTO2.setId(2L);
        assertThat(questionBankDTO1).isNotEqualTo(questionBankDTO2);
        questionBankDTO1.setId(null);
        assertThat(questionBankDTO1).isNotEqualTo(questionBankDTO2);
    }
}
