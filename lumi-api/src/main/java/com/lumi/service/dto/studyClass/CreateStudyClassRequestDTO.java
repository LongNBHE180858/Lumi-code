package com.lumi.service.dto.studyClass;

import com.lumi.domain.enumeration.ClassJoinMode;
import com.lumi.domain.enumeration.ClassStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateStudyClassRequestDTO {

    @NotBlank(message = "Tên lớp không được để trống")
    @Size(max = 255, message = "Tên lớp tối đa 255 ký tự")
    private String name;

    @Size(max = 5000, message = "Mô tả tối đa 5000 ký tự")
    private String description;

    private Long subjectId;

    private Long gradeId;

    @NotNull(message = "Vui lòng chọn cách tham gia lớp")
    private ClassJoinMode joinMode;

    @Min(value = 1, message = "Sĩ số tối đa phải lớn hơn 0")
    private Integer maxStudents;

    @NotNull(message = "Vui lòng chọn trạng thái trước khi mở lớp")
    private ClassStatus status;
}
