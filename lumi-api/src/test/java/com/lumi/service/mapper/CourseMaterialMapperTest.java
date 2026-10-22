package com.lumi.service.mapper;

import static com.lumi.domain.CourseMaterialAsserts.*;
import static com.lumi.domain.CourseMaterialTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CourseMaterialMapperTest {

    private CourseMaterialMapper courseMaterialMapper;

    @BeforeEach
    void setUp() {
        courseMaterialMapper = new CourseMaterialMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCourseMaterialSample1();
        var actual = courseMaterialMapper.toEntity(courseMaterialMapper.toDto(expected));
        assertCourseMaterialAllPropertiesEquals(expected, actual);
    }
}
