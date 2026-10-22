package com.lumi.service.dto;
import lombok.*;
/**
 * Môn và khối tổng hợp từ khóa học công khai của giảng viên.
 * Không bao gồm StudyClass; chưa có dữ liệu thì trả [].
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeachingScopeDTO {
    private Long subjectId;
    private String subjectName;
    private Long gradeId;
    private Integer gradeLevel;
}
