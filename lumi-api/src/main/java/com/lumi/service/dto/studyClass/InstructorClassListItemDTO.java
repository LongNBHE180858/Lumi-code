package com.lumi.service.dto.studyClass;

import com.lumi.domain.enumeration.ClassJoinMode;
import com.lumi.domain.enumeration.RecordStatus;
import java.time.Instant;

public record InstructorClassListItemDTO(
    Long id,
    String name,
    String description,
    String joinCode,
    ClassJoinMode joinMode,
    RecordStatus status,
    Long subjectId,
    String subjectName,
    Long gradeId,
    String gradeName,
    Integer maxStudents,
    long studentCount,
    //    long pendingStudentCount,
    Instant createdAt
) {}
