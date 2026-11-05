package com.lumi.domain;

import static com.lumi.domain.KnowledgeNodeTestSamples.*;
import static com.lumi.domain.QuestionKnowledgeLinkTestSamples.*;
import static com.lumi.domain.QuestionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuestionKnowledgeLinkTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuestionKnowledgeLink.class);
        QuestionKnowledgeLink questionKnowledgeLink1 = getQuestionKnowledgeLinkSample1();
        QuestionKnowledgeLink questionKnowledgeLink2 = new QuestionKnowledgeLink();
        assertThat(questionKnowledgeLink1).isNotEqualTo(questionKnowledgeLink2);

        questionKnowledgeLink2.setId(questionKnowledgeLink1.getId());
        assertThat(questionKnowledgeLink1).isEqualTo(questionKnowledgeLink2);

        questionKnowledgeLink2 = getQuestionKnowledgeLinkSample2();
        assertThat(questionKnowledgeLink1).isNotEqualTo(questionKnowledgeLink2);
    }

    @Test
    void questionTest() {
        QuestionKnowledgeLink questionKnowledgeLink = getQuestionKnowledgeLinkRandomSampleGenerator();
        Question questionBack = getQuestionRandomSampleGenerator();

        questionKnowledgeLink.setQuestion(questionBack);
        assertThat(questionKnowledgeLink.getQuestion()).isEqualTo(questionBack);

        questionKnowledgeLink.question(null);
        assertThat(questionKnowledgeLink.getQuestion()).isNull();
    }

    @Test
    void knowledgeNodeTest() {
        QuestionKnowledgeLink questionKnowledgeLink = getQuestionKnowledgeLinkRandomSampleGenerator();
        KnowledgeNode knowledgeNodeBack = getKnowledgeNodeRandomSampleGenerator();

        questionKnowledgeLink.setKnowledgeNode(knowledgeNodeBack);
        assertThat(questionKnowledgeLink.getKnowledgeNode()).isEqualTo(knowledgeNodeBack);

        questionKnowledgeLink.knowledgeNode(null);
        assertThat(questionKnowledgeLink.getKnowledgeNode()).isNull();
    }
}
