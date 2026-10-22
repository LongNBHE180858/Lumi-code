package com.lumi.service.mapper;

import static com.lumi.domain.GradeAsserts.*;
import static com.lumi.domain.GradeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GradeMapperTest {

    private GradeMapper gradeMapper;

    @BeforeEach
    void setUp() {
        gradeMapper = new GradeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getGradeSample1();
        var actual = gradeMapper.toEntity(gradeMapper.toDto(expected));
        assertGradeAllPropertiesEquals(expected, actual);
    }
}
