package com.lumi.service.dto.auth;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public class RefreshTokenRequestVM implements Serializable {

    @NotBlank
    private String refreshToken;

    private String deviceInfo;

    public RefreshTokenRequestVM() {}

    public RefreshTokenRequestVM(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
