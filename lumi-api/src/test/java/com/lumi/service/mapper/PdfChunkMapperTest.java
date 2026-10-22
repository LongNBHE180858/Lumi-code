package com.lumi.service.mapper;

import static com.lumi.domain.PdfChunkAsserts.*;
import static com.lumi.domain.PdfChunkTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PdfChunkMapperTest {

    private PdfChunkMapper pdfChunkMapper;

    @BeforeEach
    void setUp() {
        pdfChunkMapper = new PdfChunkMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPdfChunkSample1();
        var actual = pdfChunkMapper.toEntity(pdfChunkMapper.toDto(expected));
        assertPdfChunkAllPropertiesEquals(expected, actual);
    }
}
