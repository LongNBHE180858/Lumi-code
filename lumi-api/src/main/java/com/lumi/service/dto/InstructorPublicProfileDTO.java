package com.lumi.service.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorPublicProfileDTO {

    private Long id;

    private String fullName;

    private String imageUrl;

    private String bio;

    private List<TeachingScopeDTO> teachingScopes;
}
