package com.lumi.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

/** The server authorizes the selected child and determines the buyer and pricing. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.ALWAYS)
public class CreateCourseOrderRequestDTO {

    @NotNull
    @Positive
    private Long courseId;

    @NotNull
    @Positive
    private Long studentId;
}
