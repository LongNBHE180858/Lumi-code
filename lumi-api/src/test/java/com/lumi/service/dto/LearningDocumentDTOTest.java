package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LearningDocumentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LearningDocumentDTO.class);
        LearningDocumentDTO learningDocumentDTO1 = new LearningDocumentDTO();
        learningDocumentDTO1.setId(1L);
        LearningDocumentDTO learningDocumentDTO2 = new LearningDocumentDTO();
        assertThat(learningDocumentDTO1).isNotEqualTo(learningDocumentDTO2);
        learningDocumentDTO2.setId(learningDocumentDTO1.getId());
        assertThat(learningDocumentDTO1).isEqualTo(learningDocumentDTO2);
        learningDocumentDTO2.setId(2L);
        assertThat(learningDocumentDTO1).isNotEqualTo(learningDocumentDTO2);
        learningDocumentDTO1.setId(null);
        assertThat(learningDocumentDTO1).isNotEqualTo(learningDocumentDTO2);
    }
}
