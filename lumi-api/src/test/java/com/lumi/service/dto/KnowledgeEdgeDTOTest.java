package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class KnowledgeEdgeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(KnowledgeEdgeDTO.class);
        KnowledgeEdgeDTO knowledgeEdgeDTO1 = new KnowledgeEdgeDTO();
        knowledgeEdgeDTO1.setId(1L);
        KnowledgeEdgeDTO knowledgeEdgeDTO2 = new KnowledgeEdgeDTO();
        assertThat(knowledgeEdgeDTO1).isNotEqualTo(knowledgeEdgeDTO2);
        knowledgeEdgeDTO2.setId(knowledgeEdgeDTO1.getId());
        assertThat(knowledgeEdgeDTO1).isEqualTo(knowledgeEdgeDTO2);
        knowledgeEdgeDTO2.setId(2L);
        assertThat(knowledgeEdgeDTO1).isNotEqualTo(knowledgeEdgeDTO2);
        knowledgeEdgeDTO1.setId(null);
        assertThat(knowledgeEdgeDTO1).isNotEqualTo(knowledgeEdgeDTO2);
    }
}
