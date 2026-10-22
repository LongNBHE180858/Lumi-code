package com.lumi.service.mapper;

import static com.lumi.domain.ProctorSessionAsserts.*;
import static com.lumi.domain.ProctorSessionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProctorSessionMapperTest {

    private ProctorSessionMapper proctorSessionMapper;

    @BeforeEach
    void setUp() {
        proctorSessionMapper = new ProctorSessionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProctorSessionSample1();
        var actual = proctorSessionMapper.toEntity(proctorSessionMapper.toDto(expected));
        assertProctorSessionAllPropertiesEquals(expected, actual);
    }
}
