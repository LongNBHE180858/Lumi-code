package com.lumi.service.mapper;

import static com.lumi.domain.LearningDocumentAsserts.*;
import static com.lumi.domain.LearningDocumentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LearningDocumentMapperTest {

    private LearningDocumentMapper learningDocumentMapper;

    @BeforeEach
    void setUp() {
        learningDocumentMapper = new LearningDocumentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLearningDocumentSample1();
        var actual = learningDocumentMapper.toEntity(learningDocumentMapper.toDto(expected));
        assertLearningDocumentAllPropertiesEquals(expected, actual);
    }
}
