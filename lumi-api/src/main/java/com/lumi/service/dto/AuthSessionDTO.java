package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.AuthSession} entity.
 */
@Schema(description = "Quản lý phiên đăng nhập, thiết bị và việc thu hồi refresh token.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuthSessionDTO implements Serializable {

    private Long id;

    @Size(max = 255)
    private String deviceInfo;

    @Size(max = 255)
    private String refreshToken;

    @NotNull
    private Instant expiresAt;

    private Boolean isRevoked;

    private Instant createdAt;

    @Size(max = 255)
    private String refreshTokenHash;

    @NotNull
    private UserDTO user;

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

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getRefreshTokenHash() {
        return refreshTokenHash;
    }

    public void setRefreshTokenHash(String refreshTokenHash) {
        this.refreshTokenHash = refreshTokenHash;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuthSessionDTO)) {
            return false;
        }

        AuthSessionDTO authSessionDTO = (AuthSessionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, authSessionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuthSessionDTO{" +
            "id=" + getId() +
            ", deviceInfo='" + getDeviceInfo() + "'" +
            ", refreshToken='" + getRefreshToken() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", isRevoked='" + getIsRevoked() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", refreshTokenHash='" + getRefreshTokenHash() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
