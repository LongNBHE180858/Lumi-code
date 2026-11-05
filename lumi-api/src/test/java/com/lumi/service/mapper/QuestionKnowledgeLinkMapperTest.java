package com.lumi.service.mapper;

import static com.lumi.domain.QuestionKnowledgeLinkAsserts.*;
import static com.lumi.domain.QuestionKnowledgeLinkTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuestionKnowledgeLinkMapperTest {

    private QuestionKnowledgeLinkMapper questionKnowledgeLinkMapper;

    @BeforeEach
    void setUp() {
        questionKnowledgeLinkMapper = new QuestionKnowledgeLinkMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getQuestionKnowledgeLinkSample1();
        var actual = questionKnowledgeLinkMapper.toEntity(questionKnowledgeLinkMapper.toDto(expected));
        assertQuestionKnowledgeLinkAllPropertiesEquals(expected, actual);
    }
}
