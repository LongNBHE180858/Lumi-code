package com.lumi.domain;

import static com.lumi.domain.AssignmentStudentTestSamples.*;
import static com.lumi.domain.AssignmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssignmentStudentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssignmentStudent.class);
        AssignmentStudent assignmentStudent1 = getAssignmentStudentSample1();
        AssignmentStudent assignmentStudent2 = new AssignmentStudent();
        assertThat(assignmentStudent1).isNotEqualTo(assignmentStudent2);

        assignmentStudent2.setId(assignmentStudent1.getId());
        assertThat(assignmentStudent1).isEqualTo(assignmentStudent2);

        assignmentStudent2 = getAssignmentStudentSample2();
        assertThat(assignmentStudent1).isNotEqualTo(assignmentStudent2);
    }

    @Test
    void assignmentTest() {
        AssignmentStudent assignmentStudent = getAssignmentStudentRandomSampleGenerator();
        Assignment assignmentBack = getAssignmentRandomSampleGenerator();

        assignmentStudent.setAssignment(assignmentBack);
        assertThat(assignmentStudent.getAssignment()).isEqualTo(assignmentBack);

        assignmentStudent.assignment(null);
        assertThat(assignmentStudent.getAssignment()).isNull();
    }
}
