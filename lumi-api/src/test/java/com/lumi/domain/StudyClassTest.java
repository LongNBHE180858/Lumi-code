package com.lumi.domain;

import static com.lumi.domain.GradeTestSamples.*;
import static com.lumi.domain.StudyClassTestSamples.*;
import static com.lumi.domain.SubjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StudyClassTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StudyClass.class);
        StudyClass studyClass1 = getStudyClassSample1();
        StudyClass studyClass2 = new StudyClass();
        assertThat(studyClass1).isNotEqualTo(studyClass2);

        studyClass2.setId(studyClass1.getId());
        assertThat(studyClass1).isEqualTo(studyClass2);

        studyClass2 = getStudyClassSample2();
        assertThat(studyClass1).isNotEqualTo(studyClass2);
    }

    @Test
    void subjectTest() {
        StudyClass studyClass = getStudyClassRandomSampleGenerator();
        Subject subjectBack = getSubjectRandomSampleGenerator();

        studyClass.setSubject(subjectBack);
        assertThat(studyClass.getSubject()).isEqualTo(subjectBack);

        studyClass.subject(null);
        assertThat(studyClass.getSubject()).isNull();
    }

    @Test
    void gradeTest() {
        StudyClass studyClass = getStudyClassRandomSampleGenerator();
        Grade gradeBack = getGradeRandomSampleGenerator();

        studyClass.setGrade(gradeBack);
        assertThat(studyClass.getGrade()).isEqualTo(gradeBack);

        studyClass.grade(null);
        assertThat(studyClass.getGrade()).isNull();
    }
}
