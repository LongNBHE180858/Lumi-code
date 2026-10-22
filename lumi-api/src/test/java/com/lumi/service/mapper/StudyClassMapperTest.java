package com.lumi.service.mapper;

import static com.lumi.domain.StudyClassAsserts.*;
import static com.lumi.domain.StudyClassTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudyClassMapperTest {

    private StudyClassMapper studyClassMapper;

    @BeforeEach
    void setUp() {
        studyClassMapper = new StudyClassMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStudyClassSample1();
        var actual = studyClassMapper.toEntity(studyClassMapper.toDto(expected));
        assertStudyClassAllPropertiesEquals(expected, actual);
    }
}
