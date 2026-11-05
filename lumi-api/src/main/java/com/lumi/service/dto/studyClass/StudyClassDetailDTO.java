package com.lumi.service.dto.studyClass;

import com.lumi.domain.Grade;
import com.lumi.domain.Subject;
import com.lumi.domain.User;
import com.lumi.domain.enumeration.ClassJoinMode;
import com.lumi.domain.enumeration.RecordStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudyClassDetailDTO {

    private long id;
    private String name;
    private String description;
    private String joinCode;
    private ClassJoinMode joinMode;
    private Integer maxStudents;
    private Integer currentStudents;
    private RecordStatus status;
    private Boolean isDeleted;
    private Instant createdAt;
    private User instructor;
    private Subject subject;
    private Grade grade;
}
