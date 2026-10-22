package com.lumi.domain;

import static com.lumi.domain.QuestionBankTestSamples.*;
import static com.lumi.domain.QuestionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuestionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Question.class);
        Question question1 = getQuestionSample1();
        Question question2 = new Question();
        assertThat(question1).isNotEqualTo(question2);

        question2.setId(question1.getId());
        assertThat(question1).isEqualTo(question2);

        question2 = getQuestionSample2();
        assertThat(question1).isNotEqualTo(question2);
    }

    @Test
    void bankTest() {
        Question question = getQuestionRandomSampleGenerator();
        QuestionBank questionBankBack = getQuestionBankRandomSampleGenerator();

        question.setBank(questionBankBack);
        assertThat(question.getBank()).isEqualTo(questionBankBack);

        question.bank(null);
        assertThat(question.getBank()).isNull();
    }
}
