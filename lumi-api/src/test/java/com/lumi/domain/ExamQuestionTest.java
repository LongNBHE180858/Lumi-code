package com.lumi.domain;

import static com.lumi.domain.ExamQuestionTestSamples.*;
import static com.lumi.domain.ExamTestSamples.*;
import static com.lumi.domain.QuestionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExamQuestionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExamQuestion.class);
        ExamQuestion examQuestion1 = getExamQuestionSample1();
        ExamQuestion examQuestion2 = new ExamQuestion();
        assertThat(examQuestion1).isNotEqualTo(examQuestion2);

        examQuestion2.setId(examQuestion1.getId());
        assertThat(examQuestion1).isEqualTo(examQuestion2);

        examQuestion2 = getExamQuestionSample2();
        assertThat(examQuestion1).isNotEqualTo(examQuestion2);
    }

    @Test
    void examTest() {
        ExamQuestion examQuestion = getExamQuestionRandomSampleGenerator();
        Exam examBack = getExamRandomSampleGenerator();

        examQuestion.setExam(examBack);
        assertThat(examQuestion.getExam()).isEqualTo(examBack);

        examQuestion.exam(null);
        assertThat(examQuestion.getExam()).isNull();
    }

    @Test
    void questionTest() {
        ExamQuestion examQuestion = getExamQuestionRandomSampleGenerator();
        Question questionBack = getQuestionRandomSampleGenerator();

        examQuestion.setQuestion(questionBack);
        assertThat(examQuestion.getQuestion()).isEqualTo(questionBack);

        examQuestion.question(null);
        assertThat(examQuestion.getQuestion()).isNull();
    }
}
