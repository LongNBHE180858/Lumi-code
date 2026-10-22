
package com.lumi.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder //để dùng đc builder
@JsonInclude(JsonInclude.Include.ALWAYS) //để có field nào thiếu thì nó hiện null chứ ko mất
public class AccountProfileDTO {
    private Long id;
    private String login;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String imageUrl;
    private Set<String> authorities;
    private String bio;
    private String phoneNumber;
    private String schoolName;
    private Integer gradeLevel;
    private String verificationStatus;
}
