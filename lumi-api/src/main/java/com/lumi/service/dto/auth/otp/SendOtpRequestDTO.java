package com.lumi.service.dto.auth.otp;

import com.lumi.domain.enumeration.OtpPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public class SendOtpRequestDTO implements Serializable {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(min = 5, max = 254)
    private String email;

    @NotNull(message = "Mục đích OTP không được để trống")
    private OtpPurpose purpose;

    public SendOtpRequestDTO() {}

    public SendOtpRequestDTO(String email, OtpPurpose purpose) {
        this.email = email;
        this.purpose = purpose;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public OtpPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(OtpPurpose purpose) {
        this.purpose = purpose;
    }
}
