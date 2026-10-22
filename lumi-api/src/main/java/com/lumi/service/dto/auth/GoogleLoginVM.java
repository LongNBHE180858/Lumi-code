package com.lumi.service.dto.auth;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public class GoogleLoginVM implements Serializable {

    @NotBlank(message = "Google ID Token không được để trống")
    private String idToken;

    private String role;

    private boolean rememberMe;

    private String deviceInfo;

    public GoogleLoginVM() {}

    public GoogleLoginVM(String idToken, String role, boolean rememberMe, String deviceInfo) {
        this.idToken = idToken;
        this.role = role;
        this.rememberMe = rememberMe;
        this.deviceInfo = deviceInfo;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
