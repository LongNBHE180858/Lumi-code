package com.lumi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Quản lý phiên đăng nhập, thiết bị và việc thu hồi refresh token.
 */
@Entity
@Table(name = "auth_sessions")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuthSession implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 255)
    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    @Size(max = 255)
    @Column(name = "refresh_token", length = 255, unique = true)
    private String refreshToken;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "is_revoked")
    private Boolean isRevoked;

    @Column(name = "created_at")
    private Instant createdAt;

    @Size(max = 255)
    @Column(name = "refresh_token_hash", length = 255, unique = true)
    private String refreshTokenHash;

    @ManyToOne(optional = false)
    @NotNull
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AuthSession id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceInfo() {
        return this.deviceInfo;
    }

    public AuthSession deviceInfo(String deviceInfo) {
        this.setDeviceInfo(deviceInfo);
        return this;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public AuthSession refreshToken(String refreshToken) {
        this.setRefreshToken(refreshToken);
        return this;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Instant getExpiresAt() {
        return this.expiresAt;
    }

    public AuthSession expiresAt(Instant expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getIsRevoked() {
        return this.isRevoked;
    }

    public AuthSession isRevoked(Boolean isRevoked) {
        this.setIsRevoked(isRevoked);
        return this;
    }

    public void setIsRevoked(Boolean isRevoked) {
        this.isRevoked = isRevoked;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public AuthSession createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getRefreshTokenHash() {
        return this.refreshTokenHash;
    }

    public AuthSession refreshTokenHash(String refreshTokenHash) {
        this.setRefreshTokenHash(refreshTokenHash);
        return this;
    }

    public void setRefreshTokenHash(String refreshTokenHash) {
        this.refreshTokenHash = refreshTokenHash;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AuthSession user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuthSession)) {
            return false;
        }
        return getId() != null && getId().equals(((AuthSession) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuthSession{" +
            "id=" + getId() +
            ", deviceInfo='" + getDeviceInfo() + "'" +
            ", refreshToken='" + getRefreshToken() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", isRevoked='" + getIsRevoked() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", refreshTokenHash='" + getRefreshTokenHash() + "'" +
            "}";
    }
}
