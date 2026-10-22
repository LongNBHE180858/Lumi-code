package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CourseReviewDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseReviewDTO.class);
        CourseReviewDTO courseReviewDTO1 = new CourseReviewDTO();
        courseReviewDTO1.setId(1L);
        CourseReviewDTO courseReviewDTO2 = new CourseReviewDTO();
        assertThat(courseReviewDTO1).isNotEqualTo(courseReviewDTO2);
        courseReviewDTO2.setId(courseReviewDTO1.getId());
        assertThat(courseReviewDTO1).isEqualTo(courseReviewDTO2);
        courseReviewDTO2.setId(2L);
        assertThat(courseReviewDTO1).isNotEqualTo(courseReviewDTO2);
        courseReviewDTO1.setId(null);
        assertThat(courseReviewDTO1).isNotEqualTo(courseReviewDTO2);
    }
}
