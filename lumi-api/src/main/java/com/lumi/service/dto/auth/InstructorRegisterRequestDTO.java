package com.lumi.service.dto.auth;

import jakarta.validation.constraints.*;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class InstructorRegisterRequestDTO extends RegisterRequestDTO {

    @Size(max = 255, message = "Tên trường / đơn vị công tác tối đa 255 ký tự")
    private String schoolName;

    @Min(value = 1, message = "Khối lớp từ 1 đến 12")
    @Max(value = 12, message = "Khối lớp từ 1 đến 12")
    private Integer gradeLevel;

    private List<String> subjects; // Danh sách môn học phụ trách (Toán, Lý, Hoá...)

    @Min(value = 0, message = "Số năm kinh nghiệm không được âm")
    private Integer yearsOfExperience;

    @Size(max = 5000, message = "Tiểu sử / giới thiệu tối đa 5000 ký tự")
    private String bio;
}
