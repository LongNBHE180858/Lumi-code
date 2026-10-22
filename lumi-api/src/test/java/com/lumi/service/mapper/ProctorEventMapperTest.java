package com.lumi.service.mapper;

import static com.lumi.domain.ProctorEventAsserts.*;
import static com.lumi.domain.ProctorEventTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProctorEventMapperTest {

    private ProctorEventMapper proctorEventMapper;

    @BeforeEach
    void setUp() {
        proctorEventMapper = new ProctorEventMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProctorEventSample1();
        var actual = proctorEventMapper.toEntity(proctorEventMapper.toDto(expected));
        assertProctorEventAllPropertiesEquals(expected, actual);
    }
}
