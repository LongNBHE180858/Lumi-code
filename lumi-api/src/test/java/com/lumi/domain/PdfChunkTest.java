package com.lumi.domain;

import static com.lumi.domain.CourseMaterialTestSamples.*;
import static com.lumi.domain.PdfChunkTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PdfChunkTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PdfChunk.class);
        PdfChunk pdfChunk1 = getPdfChunkSample1();
        PdfChunk pdfChunk2 = new PdfChunk();
        assertThat(pdfChunk1).isNotEqualTo(pdfChunk2);

        pdfChunk2.setId(pdfChunk1.getId());
        assertThat(pdfChunk1).isEqualTo(pdfChunk2);

        pdfChunk2 = getPdfChunkSample2();
        assertThat(pdfChunk1).isNotEqualTo(pdfChunk2);
    }

    @Test
    void materialTest() {
        PdfChunk pdfChunk = getPdfChunkRandomSampleGenerator();
        CourseMaterial courseMaterialBack = getCourseMaterialRandomSampleGenerator();

        pdfChunk.setMaterial(courseMaterialBack);
        assertThat(pdfChunk.getMaterial()).isEqualTo(courseMaterialBack);

        pdfChunk.material(null);
        assertThat(pdfChunk.getMaterial()).isNull();
    }
}
