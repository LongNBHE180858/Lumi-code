package com.lumi.service.mapper;

import static com.lumi.domain.AssignmentStudentAsserts.*;
import static com.lumi.domain.AssignmentStudentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssignmentStudentMapperTest {

    private AssignmentStudentMapper assignmentStudentMapper;

    @BeforeEach
    void setUp() {
        assignmentStudentMapper = new AssignmentStudentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAssignmentStudentSample1();
        var actual = assignmentStudentMapper.toEntity(assignmentStudentMapper.toDto(expected));
        assertAssignmentStudentAllPropertiesEquals(expected, actual);
    }
}
