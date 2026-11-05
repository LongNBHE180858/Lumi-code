package com.lumi.service.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class StudentRegisterRequestDTO extends RegisterRequestDTO {

    @NotNull(message = "Học sinh bắt buộc phải chọn khối lớp")
    @Min(value = 1, message = "Khối lớp từ 1 đến 12")
    @Max(value = 12, message = "Khối lớp từ 1 đến 12")
    private Integer gradeLevel;

    @Size(max = 255, message = "Tên trường tối đa 255 ký tự")
    private String schoolName;
}
