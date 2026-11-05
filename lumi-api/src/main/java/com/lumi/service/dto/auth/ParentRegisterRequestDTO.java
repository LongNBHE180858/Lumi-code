package com.lumi.service.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ParentRegisterRequestDTO extends RegisterRequestDTO {

    @Min(value = 1, message = "Khối lớp của con từ 1 đến 9")
    @Max(value = 9, message = "Khối lớp của con từ 1 đến 9")
    private Integer childGradeLevel;

    private String studentCode;
}
