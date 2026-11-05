package com.lumi.domain;

import static com.lumi.domain.AiGeneratedContentTestSamples.*;
import static com.lumi.domain.GradeTestSamples.*;
import static com.lumi.domain.QuestionBankTestSamples.*;
import static com.lumi.domain.QuestionTestSamples.*;
import static com.lumi.domain.QuestionTestSamples.*;
import static com.lumi.domain.SubjectTestSamples.*;
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

    @Test
    void aiContentTest() {
        Question question = getQuestionRandomSampleGenerator();
        AiGeneratedContent aiGeneratedContentBack = getAiGeneratedContentRandomSampleGenerator();

        question.setAiContent(aiGeneratedContentBack);
        assertThat(question.getAiContent()).isEqualTo(aiGeneratedContentBack);

        question.aiContent(null);
        assertThat(question.getAiContent()).isNull();
    }

    @Test
    void subjectTest() {
        Question question = getQuestionRandomSampleGenerator();
        Subject subjectBack = getSubjectRandomSampleGenerator();

        question.setSubject(subjectBack);
        assertThat(question.getSubject()).isEqualTo(subjectBack);

        question.subject(null);
        assertThat(question.getSubject()).isNull();
    }

    @Test
    void gradeTest() {
        Question question = getQuestionRandomSampleGenerator();
        Grade gradeBack = getGradeRandomSampleGenerator();

        question.setGrade(gradeBack);
        assertThat(question.getGrade()).isEqualTo(gradeBack);

        question.grade(null);
        assertThat(question.getGrade()).isNull();
    }

    @Test
    void previousQuestionTest() {
        Question question = getQuestionRandomSampleGenerator();
        Question questionBack = getQuestionRandomSampleGenerator();

        question.setPreviousQuestion(questionBack);
        assertThat(question.getPreviousQuestion()).isEqualTo(questionBack);

        question.previousQuestion(null);
        assertThat(question.getPreviousQuestion()).isNull();
    }
}
