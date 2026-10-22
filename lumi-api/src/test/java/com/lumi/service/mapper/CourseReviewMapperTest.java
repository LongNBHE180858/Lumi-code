package com.lumi.service.mapper;

import static com.lumi.domain.CourseReviewAsserts.*;
import static com.lumi.domain.CourseReviewTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CourseReviewMapperTest {

    private CourseReviewMapper courseReviewMapper;

    @BeforeEach
    void setUp() {
        courseReviewMapper = new CourseReviewMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCourseReviewSample1();
        var actual = courseReviewMapper.toEntity(courseReviewMapper.toDto(expected));
        assertCourseReviewAllPropertiesEquals(expected, actual);
    }
}
