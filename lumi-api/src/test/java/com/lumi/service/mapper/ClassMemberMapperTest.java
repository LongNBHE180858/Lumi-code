package com.lumi.service.mapper;

import static com.lumi.domain.ClassMemberAsserts.*;
import static com.lumi.domain.ClassMemberTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClassMemberMapperTest {

    private ClassMemberMapper classMemberMapper;

    @BeforeEach
    void setUp() {
        classMemberMapper = new ClassMemberMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getClassMemberSample1();
        var actual = classMemberMapper.toEntity(classMemberMapper.toDto(expected));
        assertClassMemberAllPropertiesEquals(expected, actual);
    }
}
