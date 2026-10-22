package com.lumi.service.dto.auth.password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public class ForgotPasswordInitRequestDTO implements Serializable {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(min = 5, max = 254)
    private String email;

    public ForgotPasswordInitRequestDTO() {}

    public ForgotPasswordInitRequestDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
