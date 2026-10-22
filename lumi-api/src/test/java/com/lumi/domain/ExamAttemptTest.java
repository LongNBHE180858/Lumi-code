package com.lumi.domain;

import static com.lumi.domain.ExamAttemptTestSamples.*;
import static com.lumi.domain.ExamTestSamples.*;
import static com.lumi.domain.ProctorSessionTestSamples.*;
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
    void proctorSessionTest() {
        ExamAttempt examAttempt = getExamAttemptRandomSampleGenerator();
        ProctorSession proctorSessionBack = getProctorSessionRandomSampleGenerator();

        examAttempt.setProctorSession(proctorSessionBack);
        assertThat(examAttempt.getProctorSession()).isEqualTo(proctorSessionBack);
        assertThat(proctorSessionBack.getAttempt()).isEqualTo(examAttempt);

        examAttempt.proctorSession(null);
        assertThat(examAttempt.getProctorSession()).isNull();
        assertThat(proctorSessionBack.getAttempt()).isNull();
    }
}
