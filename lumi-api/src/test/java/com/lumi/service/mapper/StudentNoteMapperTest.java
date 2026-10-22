package com.lumi.service.mapper;

import static com.lumi.domain.StudentNoteAsserts.*;
import static com.lumi.domain.StudentNoteTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentNoteMapperTest {

    private StudentNoteMapper studentNoteMapper;

    @BeforeEach
    void setUp() {
        studentNoteMapper = new StudentNoteMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStudentNoteSample1();
        var actual = studentNoteMapper.toEntity(studentNoteMapper.toDto(expected));
        assertStudentNoteAllPropertiesEquals(expected, actual);
    }
}
