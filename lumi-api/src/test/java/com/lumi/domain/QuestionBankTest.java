package com.lumi.domain;

import static com.lumi.domain.GradeTestSamples.*;
import static com.lumi.domain.QuestionBankTestSamples.*;
import static com.lumi.domain.SubjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuestionBankTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuestionBank.class);
        QuestionBank questionBank1 = getQuestionBankSample1();
        QuestionBank questionBank2 = new QuestionBank();
        assertThat(questionBank1).isNotEqualTo(questionBank2);

        questionBank2.setId(questionBank1.getId());
        assertThat(questionBank1).isEqualTo(questionBank2);

        questionBank2 = getQuestionBankSample2();
        assertThat(questionBank1).isNotEqualTo(questionBank2);
    }

    @Test
    void subjectTest() {
        QuestionBank questionBank = getQuestionBankRandomSampleGenerator();
        Subject subjectBack = getSubjectRandomSampleGenerator();

        questionBank.setSubject(subjectBack);
        assertThat(questionBank.getSubject()).isEqualTo(subjectBack);

        questionBank.subject(null);
        assertThat(questionBank.getSubject()).isNull();
    }

    @Test
    void gradeTest() {
        QuestionBank questionBank = getQuestionBankRandomSampleGenerator();
        Grade gradeBack = getGradeRandomSampleGenerator();

        questionBank.setGrade(gradeBack);
        assertThat(questionBank.getGrade()).isEqualTo(gradeBack);

        questionBank.grade(null);
        assertThat(questionBank.getGrade()).isNull();
    }
}
