package com.lumi.service.mapper;

import static com.lumi.domain.SystemPlanAsserts.*;
import static com.lumi.domain.SystemPlanTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SystemPlanMapperTest {

    private SystemPlanMapper systemPlanMapper;

    @BeforeEach
    void setUp() {
        systemPlanMapper = new SystemPlanMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSystemPlanSample1();
        var actual = systemPlanMapper.toEntity(systemPlanMapper.toDto(expected));
        assertSystemPlanAllPropertiesEquals(expected, actual);
    }
}
