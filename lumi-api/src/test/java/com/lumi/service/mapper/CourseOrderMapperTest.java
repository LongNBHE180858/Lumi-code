package com.lumi.service.mapper;

import static com.lumi.domain.CourseOrderAsserts.*;
import static com.lumi.domain.CourseOrderTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CourseOrderMapperTest {

    private CourseOrderMapper courseOrderMapper;

    @BeforeEach
    void setUp() {
        courseOrderMapper = new CourseOrderMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCourseOrderSample1();
        var actual = courseOrderMapper.toEntity(courseOrderMapper.toDto(expected));
        assertCourseOrderAllPropertiesEquals(expected, actual);
    }
}
