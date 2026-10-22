package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CourseOrderDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseOrderDTO.class);
        CourseOrderDTO courseOrderDTO1 = new CourseOrderDTO();
        courseOrderDTO1.setId(1L);
        CourseOrderDTO courseOrderDTO2 = new CourseOrderDTO();
        assertThat(courseOrderDTO1).isNotEqualTo(courseOrderDTO2);
        courseOrderDTO2.setId(courseOrderDTO1.getId());
        assertThat(courseOrderDTO1).isEqualTo(courseOrderDTO2);
        courseOrderDTO2.setId(2L);
        assertThat(courseOrderDTO1).isNotEqualTo(courseOrderDTO2);
        courseOrderDTO1.setId(null);
        assertThat(courseOrderDTO1).isNotEqualTo(courseOrderDTO2);
    }
}
