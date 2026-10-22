package com.lumi.service.mapper;

import static com.lumi.domain.TeacherVerificationAsserts.*;
import static com.lumi.domain.TeacherVerificationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TeacherVerificationMapperTest {

    private TeacherVerificationMapper teacherVerificationMapper;

    @BeforeEach
    void setUp() {
        teacherVerificationMapper = new TeacherVerificationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTeacherVerificationSample1();
        var actual = teacherVerificationMapper.toEntity(teacherVerificationMapper.toDto(expected));
        assertTeacherVerificationAllPropertiesEquals(expected, actual);
    }
}
