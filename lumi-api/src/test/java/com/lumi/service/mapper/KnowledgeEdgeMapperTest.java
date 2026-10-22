package com.lumi.service.mapper;

import static com.lumi.domain.KnowledgeEdgeAsserts.*;
import static com.lumi.domain.KnowledgeEdgeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KnowledgeEdgeMapperTest {

    private KnowledgeEdgeMapper knowledgeEdgeMapper;

    @BeforeEach
    void setUp() {
        knowledgeEdgeMapper = new KnowledgeEdgeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getKnowledgeEdgeSample1();
        var actual = knowledgeEdgeMapper.toEntity(knowledgeEdgeMapper.toDto(expected));
        assertKnowledgeEdgeAllPropertiesEquals(expected, actual);
    }
}
