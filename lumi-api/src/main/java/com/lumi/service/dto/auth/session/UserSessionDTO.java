package com.lumi.service.dto.auth.session;

import java.io.Serializable;
import java.time.Instant;

public class UserSessionDTO implements Serializable {

    private Long id;

    private String deviceInfo;

    private Instant createdAt;

    private Instant expiresAt;

    private Boolean isRevoked;

    private Boolean isExpired;

    private Boolean isActive;

    public UserSessionDTO() {}

    public UserSessionDTO(Long id, String deviceInfo, Instant createdAt, Instant expiresAt, Boolean isRevoked) {
        this.id = id;
        this.deviceInfo = deviceInfo;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.isRevoked = isRevoked;
        Instant now = Instant.now();
        this.isExpired = expiresAt != null && expiresAt.isBefore(now);
        this.isActive = !Boolean.TRUE.equals(isRevoked) && !this.isExpired;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getIsRevoked() {
        return isRevoked;
    }

    public void setIsRevoked(Boolean isRevoked) {
        this.isRevoked = isRevoked;
    }

    public Boolean getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(Boolean isExpired) {
        this.isExpired = isExpired;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
