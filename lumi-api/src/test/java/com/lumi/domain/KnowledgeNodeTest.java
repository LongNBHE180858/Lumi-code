package com.lumi.domain;

import static com.lumi.domain.KnowledgeNodeTestSamples.*;
import static com.lumi.domain.SubjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class KnowledgeNodeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(KnowledgeNode.class);
        KnowledgeNode knowledgeNode1 = getKnowledgeNodeSample1();
        KnowledgeNode knowledgeNode2 = new KnowledgeNode();
        assertThat(knowledgeNode1).isNotEqualTo(knowledgeNode2);

        knowledgeNode2.setId(knowledgeNode1.getId());
        assertThat(knowledgeNode1).isEqualTo(knowledgeNode2);

        knowledgeNode2 = getKnowledgeNodeSample2();
        assertThat(knowledgeNode1).isNotEqualTo(knowledgeNode2);
    }

    @Test
    void subjectTest() {
        KnowledgeNode knowledgeNode = getKnowledgeNodeRandomSampleGenerator();
        Subject subjectBack = getSubjectRandomSampleGenerator();

        knowledgeNode.setSubject(subjectBack);
        assertThat(knowledgeNode.getSubject()).isEqualTo(subjectBack);

        knowledgeNode.subject(null);
        assertThat(knowledgeNode.getSubject()).isNull();
    }
}
