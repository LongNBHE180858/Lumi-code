package com.lumi.service.mapper;

import static com.lumi.domain.TeacherQuotaAsserts.*;
import static com.lumi.domain.TeacherQuotaTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TeacherQuotaMapperTest {

    private TeacherQuotaMapper teacherQuotaMapper;

    @BeforeEach
    void setUp() {
        teacherQuotaMapper = new TeacherQuotaMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTeacherQuotaSample1();
        var actual = teacherQuotaMapper.toEntity(teacherQuotaMapper.toDto(expected));
        assertTeacherQuotaAllPropertiesEquals(expected, actual);
    }
}
