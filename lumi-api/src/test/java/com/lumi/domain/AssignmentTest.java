package com.lumi.domain;

import static com.lumi.domain.AssignmentTestSamples.*;
import static com.lumi.domain.StudyClassTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssignmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Assignment.class);
        Assignment assignment1 = getAssignmentSample1();
        Assignment assignment2 = new Assignment();
        assertThat(assignment1).isNotEqualTo(assignment2);

        assignment2.setId(assignment1.getId());
        assertThat(assignment1).isEqualTo(assignment2);

        assignment2 = getAssignmentSample2();
        assertThat(assignment1).isNotEqualTo(assignment2);
    }

    @Test
    void studyClassTest() {
        Assignment assignment = getAssignmentRandomSampleGenerator();
        StudyClass studyClassBack = getStudyClassRandomSampleGenerator();

        assignment.setStudyClass(studyClassBack);
        assertThat(assignment.getStudyClass()).isEqualTo(studyClassBack);

        assignment.studyClass(null);
        assertThat(assignment.getStudyClass()).isNull();
    }
}
