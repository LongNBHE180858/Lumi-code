package com.lumi.service.dto.studyClass;

import com.lumi.domain.enumeration.ClassJoinMode;
import com.lumi.domain.enumeration.RecordStatus;
import java.time.Instant;

public record StudyClassDetailDTO(
    Long id,
    String name,
    String description,
    String joinCode,
    ClassJoinMode joinMode,
    Integer maxStudents,
    RecordStatus status,
    Instant createdAt
) {}
