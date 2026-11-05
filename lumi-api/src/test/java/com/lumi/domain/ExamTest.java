package com.lumi.domain;

import static com.lumi.domain.ExamTestSamples.*;
import static com.lumi.domain.LessonTestSamples.*;
import static com.lumi.domain.StudyClassTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExamTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Exam.class);
        Exam exam1 = getExamSample1();
        Exam exam2 = new Exam();
        assertThat(exam1).isNotEqualTo(exam2);

        exam2.setId(exam1.getId());
        assertThat(exam1).isEqualTo(exam2);

        exam2 = getExamSample2();
        assertThat(exam1).isNotEqualTo(exam2);
    }

    @Test
    void studyClassTest() {
        Exam exam = getExamRandomSampleGenerator();
        StudyClass studyClassBack = getStudyClassRandomSampleGenerator();

        exam.setStudyClass(studyClassBack);
        assertThat(exam.getStudyClass()).isEqualTo(studyClassBack);

        exam.studyClass(null);
        assertThat(exam.getStudyClass()).isNull();
    }

    @Test
    void lessonTest() {
        Exam exam = getExamRandomSampleGenerator();
        Lesson lessonBack = getLessonRandomSampleGenerator();

        exam.setLesson(lessonBack);
        assertThat(exam.getLesson()).isEqualTo(lessonBack);

        exam.lesson(null);
        assertThat(exam.getLesson()).isNull();
    }
}
