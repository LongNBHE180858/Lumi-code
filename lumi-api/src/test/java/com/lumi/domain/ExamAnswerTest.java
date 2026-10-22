package com.lumi.domain;

import static com.lumi.domain.ExamAnswerTestSamples.*;
import static com.lumi.domain.ExamAttemptTestSamples.*;
import static com.lumi.domain.QuestionOptionTestSamples.*;
import static com.lumi.domain.QuestionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExamAnswerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExamAnswer.class);
        ExamAnswer examAnswer1 = getExamAnswerSample1();
        ExamAnswer examAnswer2 = new ExamAnswer();
        assertThat(examAnswer1).isNotEqualTo(examAnswer2);

        examAnswer2.setId(examAnswer1.getId());
        assertThat(examAnswer1).isEqualTo(examAnswer2);

        examAnswer2 = getExamAnswerSample2();
        assertThat(examAnswer1).isNotEqualTo(examAnswer2);
    }

    @Test
    void attemptTest() {
        ExamAnswer examAnswer = getExamAnswerRandomSampleGenerator();
        ExamAttempt examAttemptBack = getExamAttemptRandomSampleGenerator();

        examAnswer.setAttempt(examAttemptBack);
        assertThat(examAnswer.getAttempt()).isEqualTo(examAttemptBack);

        examAnswer.attempt(null);
        assertThat(examAnswer.getAttempt()).isNull();
    }

    @Test
    void questionTest() {
        ExamAnswer examAnswer = getExamAnswerRandomSampleGenerator();
        Question questionBack = getQuestionRandomSampleGenerator();

        examAnswer.setQuestion(questionBack);
        assertThat(examAnswer.getQuestion()).isEqualTo(questionBack);

        examAnswer.question(null);
        assertThat(examAnswer.getQuestion()).isNull();
    }

    @Test
    void optionTest() {
        ExamAnswer examAnswer = getExamAnswerRandomSampleGenerator();
        QuestionOption questionOptionBack = getQuestionOptionRandomSampleGenerator();

        examAnswer.setOption(questionOptionBack);
        assertThat(examAnswer.getOption()).isEqualTo(questionOptionBack);

        examAnswer.option(null);
        assertThat(examAnswer.getOption()).isNull();
    }
}
