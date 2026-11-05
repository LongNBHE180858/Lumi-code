package com.lumi.service.dto.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lumi.config.Constants;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
public class RegisterRequestDTO implements Serializable {

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Pattern(regexp = Constants.LOGIN_REGEX, message = "Tên đăng nhập không đúng định dạng")
    @Size(min = 3, max = 50, message = "Tên đăng nhập từ 3 đến 50 ký tự")
    @JsonAlias({ "login" })
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 4, max = 100, message = "Mật khẩu từ 4 đến 100 ký tự")
    private String password;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(min = 5, max = 254)
    private String email;

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "Ngày sinh không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotBlank(message = "Họ và tên đệm không được để trống")
    @Size(max = 50)
    private String lastName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(max = 20)
    private String phoneNumber;

    @NotBlank(message = "Vai trò không được để trống")
    private String role;

    @Size(min = 2, max = 10)
    private String langKey = "vi";

    private String deviceInfo;
}
