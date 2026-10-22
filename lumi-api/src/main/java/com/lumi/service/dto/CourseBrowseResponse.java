package com.lumi.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.ALWAYS)
public class CourseBrowseResponse {

    private List<CourseItem> content;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class CourseItem {

        private Long id;

        private String title;

        private Long subjectId;

        private String subjectName;

        private Long gradeId;

        private Integer gradeLevel;

        private String gradeName;

        private String authorDisplayName;

        private BigDecimal price;

        private Long lessonCount;
    }
}
