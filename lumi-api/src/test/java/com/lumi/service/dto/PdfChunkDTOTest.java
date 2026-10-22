package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PdfChunkDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PdfChunkDTO.class);
        PdfChunkDTO pdfChunkDTO1 = new PdfChunkDTO();
        pdfChunkDTO1.setId(1L);
        PdfChunkDTO pdfChunkDTO2 = new PdfChunkDTO();
        assertThat(pdfChunkDTO1).isNotEqualTo(pdfChunkDTO2);
        pdfChunkDTO2.setId(pdfChunkDTO1.getId());
        assertThat(pdfChunkDTO1).isEqualTo(pdfChunkDTO2);
        pdfChunkDTO2.setId(2L);
        assertThat(pdfChunkDTO1).isNotEqualTo(pdfChunkDTO2);
        pdfChunkDTO1.setId(null);
        assertThat(pdfChunkDTO1).isNotEqualTo(pdfChunkDTO2);
    }
}
