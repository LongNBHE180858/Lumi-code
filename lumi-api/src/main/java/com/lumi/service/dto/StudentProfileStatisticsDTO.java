package com.lumi.service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileStatisticsDTO {

    private long enrolledCourseCount;

    private int currentStreak;

    private int longestStreak;

    private int gemsBalance;
}
