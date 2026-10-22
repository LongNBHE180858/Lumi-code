package com.lumi.service.dto.auth.twofa;

import com.lumi.domain.enumeration.OtpPurpose;
import jakarta.validation.constraints.*;

public class TwoFactorConfirmRequestDTO {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(min = 5, max = 254)
    private String email;

    private OtpPurpose purpose;

    @NotBlank(message = "Mã OTP không được để trống")
    @Pattern(regexp = "^\\d{6}$", message = "Mã OTP phải bao gồm đúng 6 chữ số")
    private String otpCode;

    private boolean is2faEnable;

    public TwoFactorConfirmRequestDTO() {}

    public TwoFactorConfirmRequestDTO(String email, OtpPurpose purpose, String otpCode, boolean is2faEnable) {
        this.email = email;
        this.purpose = purpose;
        this.otpCode = otpCode;
        this.is2faEnable = is2faEnable;
    }

    public boolean getIs2faEnable() {
        return is2faEnable;
    }

    public void setIs2faEnable(boolean is2faEnable) {
        this.is2faEnable = is2faEnable;
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

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}
