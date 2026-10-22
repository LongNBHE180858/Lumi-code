package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StudentNoteDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StudentNoteDTO.class);
        StudentNoteDTO studentNoteDTO1 = new StudentNoteDTO();
        studentNoteDTO1.setId(1L);
        StudentNoteDTO studentNoteDTO2 = new StudentNoteDTO();
        assertThat(studentNoteDTO1).isNotEqualTo(studentNoteDTO2);
        studentNoteDTO2.setId(studentNoteDTO1.getId());
        assertThat(studentNoteDTO1).isEqualTo(studentNoteDTO2);
        studentNoteDTO2.setId(2L);
        assertThat(studentNoteDTO1).isNotEqualTo(studentNoteDTO2);
        studentNoteDTO1.setId(null);
        assertThat(studentNoteDTO1).isNotEqualTo(studentNoteDTO2);
    }
}
