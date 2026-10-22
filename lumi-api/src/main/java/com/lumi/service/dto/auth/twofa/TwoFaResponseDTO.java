package com.lumi.service.dto.auth.twofa;

public class TwoFaResponseDTO {

    private boolean is2faEnable;

    private String email;

    public TwoFaResponseDTO() {}

    public TwoFaResponseDTO(boolean is2faEnable, String email) {
        this.is2faEnable = is2faEnable;
        this.email = email;
    }

    public boolean isIs2faEnable() {
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
}
