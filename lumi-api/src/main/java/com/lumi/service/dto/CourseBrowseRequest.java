package com.lumi.service.dto;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseBrowseRequest {

    private String keyword;

    private Long subjectId;

    private List<Long> gradeIds;

    @Builder.Default
    private FeeType feeType = FeeType.ALL;

    public enum FeeType {
        ALL,
        FREE,
        PAID,
    }
}
