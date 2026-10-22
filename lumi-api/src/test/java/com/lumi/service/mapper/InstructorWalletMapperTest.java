package com.lumi.service.mapper;

import static com.lumi.domain.InstructorWalletAsserts.*;
import static com.lumi.domain.InstructorWalletTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstructorWalletMapperTest {

    private InstructorWalletMapper instructorWalletMapper;

    @BeforeEach
    void setUp() {
        instructorWalletMapper = new InstructorWalletMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInstructorWalletSample1();
        var actual = instructorWalletMapper.toEntity(instructorWalletMapper.toDto(expected));
        assertInstructorWalletAllPropertiesEquals(expected, actual);
    }
}
