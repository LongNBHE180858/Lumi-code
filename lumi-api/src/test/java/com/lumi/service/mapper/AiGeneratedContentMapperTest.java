package com.lumi.service.mapper;

import static com.lumi.domain.AiGeneratedContentAsserts.*;
import static com.lumi.domain.AiGeneratedContentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AiGeneratedContentMapperTest {

    private AiGeneratedContentMapper aiGeneratedContentMapper;

    @BeforeEach
    void setUp() {
        aiGeneratedContentMapper = new AiGeneratedContentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAiGeneratedContentSample1();
        var actual = aiGeneratedContentMapper.toEntity(aiGeneratedContentMapper.toDto(expected));
        assertAiGeneratedContentAllPropertiesEquals(expected, actual);
    }
}
