package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssignmentStudentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssignmentStudentDTO.class);
        AssignmentStudentDTO assignmentStudentDTO1 = new AssignmentStudentDTO();
        assignmentStudentDTO1.setId(1L);
        AssignmentStudentDTO assignmentStudentDTO2 = new AssignmentStudentDTO();
        assertThat(assignmentStudentDTO1).isNotEqualTo(assignmentStudentDTO2);
        assignmentStudentDTO2.setId(assignmentStudentDTO1.getId());
        assertThat(assignmentStudentDTO1).isEqualTo(assignmentStudentDTO2);
        assignmentStudentDTO2.setId(2L);
        assertThat(assignmentStudentDTO1).isNotEqualTo(assignmentStudentDTO2);
        assignmentStudentDTO1.setId(null);
        assertThat(assignmentStudentDTO1).isNotEqualTo(assignmentStudentDTO2);
    }
}
