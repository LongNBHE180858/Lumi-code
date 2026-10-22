package com.lumi.service.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorPublicCourseDTO {

    private Long id;

    private String title;

    private String description;

    private Long subjectId;

    private String subjectName;

    private Long gradeId;

    private Integer gradeLevel;

    private BigDecimal price;

    private Integer validDurationDays;
}
