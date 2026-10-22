package com.lumi.service.mapper;

import static com.lumi.domain.StudentStreakAsserts.*;
import static com.lumi.domain.StudentStreakTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentStreakMapperTest {

    private StudentStreakMapper studentStreakMapper;

    @BeforeEach
    void setUp() {
        studentStreakMapper = new StudentStreakMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStudentStreakSample1();
        var actual = studentStreakMapper.toEntity(studentStreakMapper.toDto(expected));
        assertStudentStreakAllPropertiesEquals(expected, actual);
    }
}
