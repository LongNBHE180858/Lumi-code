package com.lumi.service.dto.auth.otp;

import java.io.Serializable;

public class OtpResponseDTO implements Serializable {

    private String message;

    private long cooldownSeconds;

    private long expiresInSeconds;

    private boolean verified;

    public OtpResponseDTO() {}

    public OtpResponseDTO(String message, long cooldownSeconds, long expiresInSeconds, boolean verified) {
        this.message = message;
        this.cooldownSeconds = cooldownSeconds;
        this.expiresInSeconds = expiresInSeconds;
        this.verified = verified;
    }

    public static OtpResponseDTO sent(String message, long cooldownSeconds, long expiresInSeconds) {
        return new OtpResponseDTO(message, cooldownSeconds, expiresInSeconds, false);
    }

    public static OtpResponseDTO verified(String message) {
        return new OtpResponseDTO(message, 0, 0, true);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getCooldownSeconds() {
        return cooldownSeconds;
    }

    public void setCooldownSeconds(long cooldownSeconds) {
        this.cooldownSeconds = cooldownSeconds;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
