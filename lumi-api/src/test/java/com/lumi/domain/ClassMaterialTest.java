package com.lumi.domain;

import static com.lumi.domain.ClassMaterialTestSamples.*;
import static com.lumi.domain.LearningDocumentTestSamples.*;
import static com.lumi.domain.StudyClassTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClassMaterialTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClassMaterial.class);
        ClassMaterial classMaterial1 = getClassMaterialSample1();
        ClassMaterial classMaterial2 = new ClassMaterial();
        assertThat(classMaterial1).isNotEqualTo(classMaterial2);

        classMaterial2.setId(classMaterial1.getId());
        assertThat(classMaterial1).isEqualTo(classMaterial2);

        classMaterial2 = getClassMaterialSample2();
        assertThat(classMaterial1).isNotEqualTo(classMaterial2);
    }

    @Test
    void studyClassTest() {
        ClassMaterial classMaterial = getClassMaterialRandomSampleGenerator();
        StudyClass studyClassBack = getStudyClassRandomSampleGenerator();

        classMaterial.setStudyClass(studyClassBack);
        assertThat(classMaterial.getStudyClass()).isEqualTo(studyClassBack);

        classMaterial.studyClass(null);
        assertThat(classMaterial.getStudyClass()).isNull();
    }

    @Test
    void learningDocumentTest() {
        ClassMaterial classMaterial = getClassMaterialRandomSampleGenerator();
        LearningDocument learningDocumentBack = getLearningDocumentRandomSampleGenerator();

        classMaterial.setLearningDocument(learningDocumentBack);
        assertThat(classMaterial.getLearningDocument()).isEqualTo(learningDocumentBack);
        assertThat(learningDocumentBack.getClassMaterial()).isEqualTo(classMaterial);

        classMaterial.learningDocument(null);
        assertThat(classMaterial.getLearningDocument()).isNull();
        assertThat(learningDocumentBack.getClassMaterial()).isNull();
    }
}
