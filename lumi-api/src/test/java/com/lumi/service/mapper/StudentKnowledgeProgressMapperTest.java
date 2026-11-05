package com.lumi.service.mapper;

import static com.lumi.domain.StudentKnowledgeProgressAsserts.*;
import static com.lumi.domain.StudentKnowledgeProgressTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentKnowledgeProgressMapperTest {

    private StudentKnowledgeProgressMapper studentKnowledgeProgressMapper;

    @BeforeEach
    void setUp() {
        studentKnowledgeProgressMapper = new StudentKnowledgeProgressMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStudentKnowledgeProgressSample1();
        var actual = studentKnowledgeProgressMapper.toEntity(studentKnowledgeProgressMapper.toDto(expected));
        assertStudentKnowledgeProgressAllPropertiesEquals(expected, actual);
    }
}
