package com.lumi.service.mapper;

import static com.lumi.domain.KnowledgeNodeAsserts.*;
import static com.lumi.domain.KnowledgeNodeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KnowledgeNodeMapperTest {

    private KnowledgeNodeMapper knowledgeNodeMapper;

    @BeforeEach
    void setUp() {
        knowledgeNodeMapper = new KnowledgeNodeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getKnowledgeNodeSample1();
        var actual = knowledgeNodeMapper.toEntity(knowledgeNodeMapper.toDto(expected));
        assertKnowledgeNodeAllPropertiesEquals(expected, actual);
    }
}
