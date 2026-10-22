package com.lumi.service.mapper;

import static com.lumi.domain.ExamAnswerAsserts.*;
import static com.lumi.domain.ExamAnswerTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExamAnswerMapperTest {

    private ExamAnswerMapper examAnswerMapper;

    @BeforeEach
    void setUp() {
        examAnswerMapper = new ExamAnswerMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getExamAnswerSample1();
        var actual = examAnswerMapper.toEntity(examAnswerMapper.toDto(expected));
        assertExamAnswerAllPropertiesEquals(expected, actual);
    }
}
