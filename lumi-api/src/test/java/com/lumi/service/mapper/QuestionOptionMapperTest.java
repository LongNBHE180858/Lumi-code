package com.lumi.service.mapper;

import static com.lumi.domain.QuestionOptionAsserts.*;
import static com.lumi.domain.QuestionOptionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuestionOptionMapperTest {

    private QuestionOptionMapper questionOptionMapper;

    @BeforeEach
    void setUp() {
        questionOptionMapper = new QuestionOptionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getQuestionOptionSample1();
        var actual = questionOptionMapper.toEntity(questionOptionMapper.toDto(expected));
        assertQuestionOptionAllPropertiesEquals(expected, actual);
    }
}
