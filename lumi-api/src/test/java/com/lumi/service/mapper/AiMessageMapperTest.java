package com.lumi.service.mapper;

import static com.lumi.domain.AiMessageAsserts.*;
import static com.lumi.domain.AiMessageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AiMessageMapperTest {

    private AiMessageMapper aiMessageMapper;

    @BeforeEach
    void setUp() {
        aiMessageMapper = new AiMessageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAiMessageSample1();
        var actual = aiMessageMapper.toEntity(aiMessageMapper.toDto(expected));
        assertAiMessageAllPropertiesEquals(expected, actual);
    }
}
