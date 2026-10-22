package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StudentStreakDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StudentStreakDTO.class);
        StudentStreakDTO studentStreakDTO1 = new StudentStreakDTO();
        studentStreakDTO1.setId(1L);
        StudentStreakDTO studentStreakDTO2 = new StudentStreakDTO();
        assertThat(studentStreakDTO1).isNotEqualTo(studentStreakDTO2);
        studentStreakDTO2.setId(studentStreakDTO1.getId());
        assertThat(studentStreakDTO1).isEqualTo(studentStreakDTO2);
        studentStreakDTO2.setId(2L);
        assertThat(studentStreakDTO1).isNotEqualTo(studentStreakDTO2);
        studentStreakDTO1.setId(null);
        assertThat(studentStreakDTO1).isNotEqualTo(studentStreakDTO2);
    }
}
