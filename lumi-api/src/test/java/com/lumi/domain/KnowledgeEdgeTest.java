package com.lumi.domain;

import static com.lumi.domain.KnowledgeEdgeTestSamples.*;
import static com.lumi.domain.KnowledgeNodeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class KnowledgeEdgeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(KnowledgeEdge.class);
        KnowledgeEdge knowledgeEdge1 = getKnowledgeEdgeSample1();
        KnowledgeEdge knowledgeEdge2 = new KnowledgeEdge();
        assertThat(knowledgeEdge1).isNotEqualTo(knowledgeEdge2);

        knowledgeEdge2.setId(knowledgeEdge1.getId());
        assertThat(knowledgeEdge1).isEqualTo(knowledgeEdge2);

        knowledgeEdge2 = getKnowledgeEdgeSample2();
        assertThat(knowledgeEdge1).isNotEqualTo(knowledgeEdge2);
    }

    @Test
    void fromNodeTest() {
        KnowledgeEdge knowledgeEdge = getKnowledgeEdgeRandomSampleGenerator();
        KnowledgeNode knowledgeNodeBack = getKnowledgeNodeRandomSampleGenerator();

        knowledgeEdge.setFromNode(knowledgeNodeBack);
        assertThat(knowledgeEdge.getFromNode()).isEqualTo(knowledgeNodeBack);

        knowledgeEdge.fromNode(null);
        assertThat(knowledgeEdge.getFromNode()).isNull();
    }

    @Test
    void toNodeTest() {
        KnowledgeEdge knowledgeEdge = getKnowledgeEdgeRandomSampleGenerator();
        KnowledgeNode knowledgeNodeBack = getKnowledgeNodeRandomSampleGenerator();

        knowledgeEdge.setToNode(knowledgeNodeBack);
        assertThat(knowledgeEdge.getToNode()).isEqualTo(knowledgeNodeBack);

        knowledgeEdge.toNode(null);
        assertThat(knowledgeEdge.getToNode()).isNull();
    }
}
