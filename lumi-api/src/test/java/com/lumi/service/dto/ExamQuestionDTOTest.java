package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExamQuestionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExamQuestionDTO.class);
        ExamQuestionDTO examQuestionDTO1 = new ExamQuestionDTO();
        examQuestionDTO1.setId(1L);
        ExamQuestionDTO examQuestionDTO2 = new ExamQuestionDTO();
        assertThat(examQuestionDTO1).isNotEqualTo(examQuestionDTO2);
        examQuestionDTO2.setId(examQuestionDTO1.getId());
        assertThat(examQuestionDTO1).isEqualTo(examQuestionDTO2);
        examQuestionDTO2.setId(2L);
        assertThat(examQuestionDTO1).isNotEqualTo(examQuestionDTO2);
        examQuestionDTO1.setId(null);
        assertThat(examQuestionDTO1).isNotEqualTo(examQuestionDTO2);
    }
}
