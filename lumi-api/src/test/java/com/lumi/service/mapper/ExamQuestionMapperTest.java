package com.lumi.service.mapper;

import static com.lumi.domain.ExamQuestionAsserts.*;
import static com.lumi.domain.ExamQuestionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExamQuestionMapperTest {

    private ExamQuestionMapper examQuestionMapper;

    @BeforeEach
    void setUp() {
        examQuestionMapper = new ExamQuestionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getExamQuestionSample1();
        var actual = examQuestionMapper.toEntity(examQuestionMapper.toDto(expected));
        assertExamQuestionAllPropertiesEquals(expected, actual);
    }
}
