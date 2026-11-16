package com.lumi.web.rest.vm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * View Model for 2FA Login verification.
 */
public class Login2faVM {

    @NotBlank
    private String challengeId;

    @NotBlank
    @Pattern(regexp = "\\d{6}")
    private String otpCode;

    private String deviceInfo;

    public Login2faVM() {}

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    @Override
    public String toString() {
        return "Login2faVM{challengeId='" + challengeId + "'}";
    }
}
