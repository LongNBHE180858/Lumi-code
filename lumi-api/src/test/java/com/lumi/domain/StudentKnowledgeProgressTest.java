package com.lumi.domain;

import static com.lumi.domain.KnowledgeNodeTestSamples.*;
import static com.lumi.domain.StudentKnowledgeProgressTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StudentKnowledgeProgressTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StudentKnowledgeProgress.class);
        StudentKnowledgeProgress studentKnowledgeProgress1 = getStudentKnowledgeProgressSample1();
        StudentKnowledgeProgress studentKnowledgeProgress2 = new StudentKnowledgeProgress();
        assertThat(studentKnowledgeProgress1).isNotEqualTo(studentKnowledgeProgress2);

        studentKnowledgeProgress2.setId(studentKnowledgeProgress1.getId());
        assertThat(studentKnowledgeProgress1).isEqualTo(studentKnowledgeProgress2);

        studentKnowledgeProgress2 = getStudentKnowledgeProgressSample2();
        assertThat(studentKnowledgeProgress1).isNotEqualTo(studentKnowledgeProgress2);
    }

    @Test
    void knowledgeNodeTest() {
        StudentKnowledgeProgress studentKnowledgeProgress = getStudentKnowledgeProgressRandomSampleGenerator();
        KnowledgeNode knowledgeNodeBack = getKnowledgeNodeRandomSampleGenerator();

        studentKnowledgeProgress.setKnowledgeNode(knowledgeNodeBack);
        assertThat(studentKnowledgeProgress.getKnowledgeNode()).isEqualTo(knowledgeNodeBack);

        studentKnowledgeProgress.knowledgeNode(null);
        assertThat(studentKnowledgeProgress.getKnowledgeNode()).isNull();
    }
}
