package com.lumi.service.dto.auth.twofa;

import com.lumi.domain.enumeration.OtpPurpose;

public class TwoFaInitRequestDTO {

    private String email;

    private OtpPurpose purpose;

    public TwoFaInitRequestDTO() {}

    public TwoFaInitRequestDTO(String email, OtpPurpose puspose) {
        this.email = email;
        this.purpose = puspose;
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
