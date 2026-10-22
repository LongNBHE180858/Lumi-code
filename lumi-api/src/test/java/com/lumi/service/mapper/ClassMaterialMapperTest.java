package com.lumi.service.mapper;

import static com.lumi.domain.ClassMaterialAsserts.*;
import static com.lumi.domain.ClassMaterialTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClassMaterialMapperTest {

    private ClassMaterialMapper classMaterialMapper;

    @BeforeEach
    void setUp() {
        classMaterialMapper = new ClassMaterialMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getClassMaterialSample1();
        var actual = classMaterialMapper.toEntity(classMaterialMapper.toDto(expected));
        assertClassMaterialAllPropertiesEquals(expected, actual);
    }
}
