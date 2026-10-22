package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExamAnswerDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExamAnswerDTO.class);
        ExamAnswerDTO examAnswerDTO1 = new ExamAnswerDTO();
        examAnswerDTO1.setId(1L);
        ExamAnswerDTO examAnswerDTO2 = new ExamAnswerDTO();
        assertThat(examAnswerDTO1).isNotEqualTo(examAnswerDTO2);
        examAnswerDTO2.setId(examAnswerDTO1.getId());
        assertThat(examAnswerDTO1).isEqualTo(examAnswerDTO2);
        examAnswerDTO2.setId(2L);
        assertThat(examAnswerDTO1).isNotEqualTo(examAnswerDTO2);
        examAnswerDTO1.setId(null);
        assertThat(examAnswerDTO1).isNotEqualTo(examAnswerDTO2);
    }
}
