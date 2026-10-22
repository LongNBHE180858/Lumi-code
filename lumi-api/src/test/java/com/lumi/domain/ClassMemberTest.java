package com.lumi.domain;

import static com.lumi.domain.ClassMemberTestSamples.*;
import static com.lumi.domain.StudyClassTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClassMemberTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClassMember.class);
        ClassMember classMember1 = getClassMemberSample1();
        ClassMember classMember2 = new ClassMember();
        assertThat(classMember1).isNotEqualTo(classMember2);

        classMember2.setId(classMember1.getId());
        assertThat(classMember1).isEqualTo(classMember2);

        classMember2 = getClassMemberSample2();
        assertThat(classMember1).isNotEqualTo(classMember2);
    }

    @Test
    void studyClassTest() {
        ClassMember classMember = getClassMemberRandomSampleGenerator();
        StudyClass studyClassBack = getStudyClassRandomSampleGenerator();

        classMember.setStudyClass(studyClassBack);
        assertThat(classMember.getStudyClass()).isEqualTo(studyClassBack);

        classMember.studyClass(null);
        assertThat(classMember.getStudyClass()).isNull();
    }
}
