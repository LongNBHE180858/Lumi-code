package com.lumi.domain;

import static com.lumi.domain.ContentReportTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ContentReportTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ContentReport.class);
        ContentReport contentReport1 = getContentReportSample1();
        ContentReport contentReport2 = new ContentReport();
        assertThat(contentReport1).isNotEqualTo(contentReport2);

        contentReport2.setId(contentReport1.getId());
        assertThat(contentReport1).isEqualTo(contentReport2);

        contentReport2 = getContentReportSample2();
        assertThat(contentReport1).isNotEqualTo(contentReport2);
    }
}
