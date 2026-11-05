package com.lumi.domain;

import static com.lumi.domain.ClassMaterialTestSamples.*;
import static com.lumi.domain.CourseMaterialTestSamples.*;
import static com.lumi.domain.GradeTestSamples.*;
import static com.lumi.domain.LearningDocumentTestSamples.*;
import static com.lumi.domain.SubjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LearningDocumentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LearningDocument.class);
        LearningDocument learningDocument1 = getLearningDocumentSample1();
        LearningDocument learningDocument2 = new LearningDocument();
        assertThat(learningDocument1).isNotEqualTo(learningDocument2);

        learningDocument2.setId(learningDocument1.getId());
        assertThat(learningDocument1).isEqualTo(learningDocument2);

        learningDocument2 = getLearningDocumentSample2();
        assertThat(learningDocument1).isNotEqualTo(learningDocument2);
    }

    @Test
    void courseMaterialTest() {
        LearningDocument learningDocument = getLearningDocumentRandomSampleGenerator();
        CourseMaterial courseMaterialBack = getCourseMaterialRandomSampleGenerator();

        learningDocument.setCourseMaterial(courseMaterialBack);
        assertThat(learningDocument.getCourseMaterial()).isEqualTo(courseMaterialBack);

        learningDocument.courseMaterial(null);
        assertThat(learningDocument.getCourseMaterial()).isNull();
    }

    @Test
    void classMaterialTest() {
        LearningDocument learningDocument = getLearningDocumentRandomSampleGenerator();
        ClassMaterial classMaterialBack = getClassMaterialRandomSampleGenerator();

        learningDocument.setClassMaterial(classMaterialBack);
        assertThat(learningDocument.getClassMaterial()).isEqualTo(classMaterialBack);

        learningDocument.classMaterial(null);
        assertThat(learningDocument.getClassMaterial()).isNull();
    }

    @Test
    void subjectTest() {
        LearningDocument learningDocument = getLearningDocumentRandomSampleGenerator();
        Subject subjectBack = getSubjectRandomSampleGenerator();

        learningDocument.setSubject(subjectBack);
        assertThat(learningDocument.getSubject()).isEqualTo(subjectBack);

        learningDocument.subject(null);
        assertThat(learningDocument.getSubject()).isNull();
    }

    @Test
    void gradeTest() {
        LearningDocument learningDocument = getLearningDocumentRandomSampleGenerator();
        Grade gradeBack = getGradeRandomSampleGenerator();

        learningDocument.setGrade(gradeBack);
        assertThat(learningDocument.getGrade()).isEqualTo(gradeBack);

        learningDocument.grade(null);
        assertThat(learningDocument.getGrade()).isNull();
    }
}
