package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssignmentSubmissionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssignmentSubmissionDTO.class);
        AssignmentSubmissionDTO assignmentSubmissionDTO1 = new AssignmentSubmissionDTO();
        assignmentSubmissionDTO1.setId(1L);
        AssignmentSubmissionDTO assignmentSubmissionDTO2 = new AssignmentSubmissionDTO();
        assertThat(assignmentSubmissionDTO1).isNotEqualTo(assignmentSubmissionDTO2);
        assignmentSubmissionDTO2.setId(assignmentSubmissionDTO1.getId());
        assertThat(assignmentSubmissionDTO1).isEqualTo(assignmentSubmissionDTO2);
        assignmentSubmissionDTO2.setId(2L);
        assertThat(assignmentSubmissionDTO1).isNotEqualTo(assignmentSubmissionDTO2);
        assignmentSubmissionDTO1.setId(null);
        assertThat(assignmentSubmissionDTO1).isNotEqualTo(assignmentSubmissionDTO2);
    }
}
