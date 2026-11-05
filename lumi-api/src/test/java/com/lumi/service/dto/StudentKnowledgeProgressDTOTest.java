package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StudentKnowledgeProgressDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StudentKnowledgeProgressDTO.class);
        StudentKnowledgeProgressDTO studentKnowledgeProgressDTO1 = new StudentKnowledgeProgressDTO();
        studentKnowledgeProgressDTO1.setId(1L);
        StudentKnowledgeProgressDTO studentKnowledgeProgressDTO2 = new StudentKnowledgeProgressDTO();
        assertThat(studentKnowledgeProgressDTO1).isNotEqualTo(studentKnowledgeProgressDTO2);
        studentKnowledgeProgressDTO2.setId(studentKnowledgeProgressDTO1.getId());
        assertThat(studentKnowledgeProgressDTO1).isEqualTo(studentKnowledgeProgressDTO2);
        studentKnowledgeProgressDTO2.setId(2L);
        assertThat(studentKnowledgeProgressDTO1).isNotEqualTo(studentKnowledgeProgressDTO2);
        studentKnowledgeProgressDTO1.setId(null);
        assertThat(studentKnowledgeProgressDTO1).isNotEqualTo(studentKnowledgeProgressDTO2);
    }
}
