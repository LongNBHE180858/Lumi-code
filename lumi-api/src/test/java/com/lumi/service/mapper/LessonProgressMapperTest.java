package com.lumi.service.mapper;

import static com.lumi.domain.LessonProgressAsserts.*;
import static com.lumi.domain.LessonProgressTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LessonProgressMapperTest {

    private LessonProgressMapper lessonProgressMapper;

    @BeforeEach
    void setUp() {
        lessonProgressMapper = new LessonProgressMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLessonProgressSample1();
        var actual = lessonProgressMapper.toEntity(lessonProgressMapper.toDto(expected));
        assertLessonProgressAllPropertiesEquals(expected, actual);
    }
}
