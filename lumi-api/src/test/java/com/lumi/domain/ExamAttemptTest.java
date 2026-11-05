package com.lumi.domain;

import static com.lumi.domain.AssignmentTestSamples.*;
import static com.lumi.domain.ExamAttemptTestSamples.*;
import static com.lumi.domain.ExamTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExamAttemptTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExamAttempt.class);
        ExamAttempt examAttempt1 = getExamAttemptSample1();
        ExamAttempt examAttempt2 = new ExamAttempt();
        assertThat(examAttempt1).isNotEqualTo(examAttempt2);

        examAttempt2.setId(examAttempt1.getId());
        assertThat(examAttempt1).isEqualTo(examAttempt2);

        examAttempt2 = getExamAttemptSample2();
        assertThat(examAttempt1).isNotEqualTo(examAttempt2);
    }

    @Test
    void examTest() {
        ExamAttempt examAttempt = getExamAttemptRandomSampleGenerator();
        Exam examBack = getExamRandomSampleGenerator();

        examAttempt.setExam(examBack);
        assertThat(examAttempt.getExam()).isEqualTo(examBack);

        examAttempt.exam(null);
        assertThat(examAttempt.getExam()).isNull();
    }

    @Test
    void assignmentTest() {
        ExamAttempt examAttempt = getExamAttemptRandomSampleGenerator();
        Assignment assignmentBack = getAssignmentRandomSampleGenerator();

        examAttempt.setAssignment(assignmentBack);
        assertThat(examAttempt.getAssignment()).isEqualTo(assignmentBack);

        examAttempt.assignment(null);
        assertThat(examAttempt.getAssignment()).isNull();
    }
}
