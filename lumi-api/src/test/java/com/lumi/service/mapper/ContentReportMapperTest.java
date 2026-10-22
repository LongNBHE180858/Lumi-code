package com.lumi.service.mapper;

import static com.lumi.domain.ContentReportAsserts.*;
import static com.lumi.domain.ContentReportTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContentReportMapperTest {

    private ContentReportMapper contentReportMapper;

    @BeforeEach
    void setUp() {
        contentReportMapper = new ContentReportMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getContentReportSample1();
        var actual = contentReportMapper.toEntity(contentReportMapper.toDto(expected));
        assertContentReportAllPropertiesEquals(expected, actual);
    }
}
