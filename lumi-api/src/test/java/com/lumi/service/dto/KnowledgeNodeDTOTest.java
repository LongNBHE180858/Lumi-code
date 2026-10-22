package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class KnowledgeNodeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(KnowledgeNodeDTO.class);
        KnowledgeNodeDTO knowledgeNodeDTO1 = new KnowledgeNodeDTO();
        knowledgeNodeDTO1.setId(1L);
        KnowledgeNodeDTO knowledgeNodeDTO2 = new KnowledgeNodeDTO();
        assertThat(knowledgeNodeDTO1).isNotEqualTo(knowledgeNodeDTO2);
        knowledgeNodeDTO2.setId(knowledgeNodeDTO1.getId());
        assertThat(knowledgeNodeDTO1).isEqualTo(knowledgeNodeDTO2);
        knowledgeNodeDTO2.setId(2L);
        assertThat(knowledgeNodeDTO1).isNotEqualTo(knowledgeNodeDTO2);
        knowledgeNodeDTO1.setId(null);
        assertThat(knowledgeNodeDTO1).isNotEqualTo(knowledgeNodeDTO2);
    }
}
