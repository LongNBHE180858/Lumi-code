package com.lumi.service.mapper;

import static com.lumi.domain.CourseReviewRequestAsserts.*;
import static com.lumi.domain.CourseReviewRequestTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CourseReviewRequestMapperTest {

    private CourseReviewRequestMapper courseReviewRequestMapper;

    @BeforeEach
    void setUp() {
        courseReviewRequestMapper = new CourseReviewRequestMapperImpl();
        org.springframework.test.util.ReflectionTestUtils.setField(courseReviewRequestMapper, "courseMapper", new CourseMapperImpl());
        org.springframework.test.util.ReflectionTestUtils.setField(courseReviewRequestMapper, "userMapper", new UserMapper());
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCourseReviewRequestSample1();
        var actual = courseReviewRequestMapper.toEntity(courseReviewRequestMapper.toDto(expected));
        assertCourseReviewRequestAllPropertiesEquals(expected, actual);
    }
}
