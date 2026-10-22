package com.lumi.service.mapper;

import static com.lumi.domain.AiConversationAsserts.*;
import static com.lumi.domain.AiConversationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AiConversationMapperTest {

    private AiConversationMapper aiConversationMapper;

    @BeforeEach
    void setUp() {
        aiConversationMapper = new AiConversationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAiConversationSample1();
        var actual = aiConversationMapper.toEntity(aiConversationMapper.toDto(expected));
        assertAiConversationAllPropertiesEquals(expected, actual);
    }
}
