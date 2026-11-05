package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CourseReviewRequestDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseReviewRequestDTO.class);
        CourseReviewRequestDTO courseReviewRequestDTO1 = new CourseReviewRequestDTO();
        courseReviewRequestDTO1.setId(1L);
        CourseReviewRequestDTO courseReviewRequestDTO2 = new CourseReviewRequestDTO();
        assertThat(courseReviewRequestDTO1).isNotEqualTo(courseReviewRequestDTO2);
        courseReviewRequestDTO2.setId(courseReviewRequestDTO1.getId());
        assertThat(courseReviewRequestDTO1).isEqualTo(courseReviewRequestDTO2);
        courseReviewRequestDTO2.setId(2L);
        assertThat(courseReviewRequestDTO1).isNotEqualTo(courseReviewRequestDTO2);
        courseReviewRequestDTO1.setId(null);
        assertThat(courseReviewRequestDTO1).isNotEqualTo(courseReviewRequestDTO2);
    }
}
