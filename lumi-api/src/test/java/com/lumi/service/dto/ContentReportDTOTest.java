package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ContentReportDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ContentReportDTO.class);
        ContentReportDTO contentReportDTO1 = new ContentReportDTO();
        contentReportDTO1.setId(1L);
        ContentReportDTO contentReportDTO2 = new ContentReportDTO();
        assertThat(contentReportDTO1).isNotEqualTo(contentReportDTO2);
        contentReportDTO2.setId(contentReportDTO1.getId());
        assertThat(contentReportDTO1).isEqualTo(contentReportDTO2);
        contentReportDTO2.setId(2L);
        assertThat(contentReportDTO1).isNotEqualTo(contentReportDTO2);
        contentReportDTO1.setId(null);
        assertThat(contentReportDTO1).isNotEqualTo(contentReportDTO2);
    }
}
