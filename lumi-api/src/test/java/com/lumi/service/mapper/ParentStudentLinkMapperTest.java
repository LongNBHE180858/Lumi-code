package com.lumi.service.mapper;

import static com.lumi.domain.ParentStudentLinkAsserts.*;
import static com.lumi.domain.ParentStudentLinkTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParentStudentLinkMapperTest {

    private ParentStudentLinkMapper parentStudentLinkMapper;

    @BeforeEach
    void setUp() {
        parentStudentLinkMapper = new ParentStudentLinkMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getParentStudentLinkSample1();
        var actual = parentStudentLinkMapper.toEntity(parentStudentLinkMapper.toDto(expected));
        assertParentStudentLinkAllPropertiesEquals(expected, actual);
    }
}
