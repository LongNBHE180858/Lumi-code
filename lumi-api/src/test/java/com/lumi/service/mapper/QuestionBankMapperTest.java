package com.lumi.service.mapper;

import static com.lumi.domain.QuestionBankAsserts.*;
import static com.lumi.domain.QuestionBankTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuestionBankMapperTest {

    private QuestionBankMapper questionBankMapper;

    @BeforeEach
    void setUp() {
        questionBankMapper = new QuestionBankMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getQuestionBankSample1();
        var actual = questionBankMapper.toEntity(questionBankMapper.toDto(expected));
        assertQuestionBankAllPropertiesEquals(expected, actual);
    }
}
