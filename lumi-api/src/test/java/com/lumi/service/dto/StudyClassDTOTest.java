package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StudyClassDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StudyClassDTO.class);
        StudyClassDTO studyClassDTO1 = new StudyClassDTO();
        studyClassDTO1.setId(1L);
        StudyClassDTO studyClassDTO2 = new StudyClassDTO();
        assertThat(studyClassDTO1).isNotEqualTo(studyClassDTO2);
        studyClassDTO2.setId(studyClassDTO1.getId());
        assertThat(studyClassDTO1).isEqualTo(studyClassDTO2);
        studyClassDTO2.setId(2L);
        assertThat(studyClassDTO1).isNotEqualTo(studyClassDTO2);
        studyClassDTO1.setId(null);
        assertThat(studyClassDTO1).isNotEqualTo(studyClassDTO2);
    }
}
