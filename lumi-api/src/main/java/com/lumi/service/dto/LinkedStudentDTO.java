package com.lumi.service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkedStudentDTO {
    private Long linkId;
    private Long studentId;
    private String fullName;
    private String status;
}
