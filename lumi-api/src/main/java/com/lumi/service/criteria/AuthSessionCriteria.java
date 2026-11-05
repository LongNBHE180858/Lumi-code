package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.AuthSession} entity. This class is used
 * in {@link com.lumi.web.rest.AuthSessionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /auth-sessions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuthSessionCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter deviceInfo;

    private StringFilter refreshToken;

    private InstantFilter expiresAt;

    private BooleanFilter isRevoked;

    private InstantFilter createdAt;

    private StringFilter refreshTokenHash;

    private LongFilter userId;

    private Boolean distinct;

    public AuthSessionCriteria() {}

    public AuthSessionCriteria(AuthSessionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.deviceInfo = other.optionalDeviceInfo().map(StringFilter::copy).orElse(null);
        this.refreshToken = other.optionalRefreshToken().map(StringFilter::copy).orElse(null);
        this.expiresAt = other.optionalExpiresAt().map(InstantFilter::copy).orElse(null);
        this.isRevoked = other.optionalIsRevoked().map(BooleanFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.refreshTokenHash = other.optionalRefreshTokenHash().map(StringFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AuthSessionCriteria copy() {
        return new AuthSessionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getDeviceInfo() {
        return deviceInfo;
    }

    public Optional<StringFilter> optionalDeviceInfo() {
        return Optional.ofNullable(deviceInfo);
    }

    public StringFilter deviceInfo() {
        if (deviceInfo == null) {
            setDeviceInfo(new StringFilter());
        }
        return deviceInfo;
    }

    public void setDeviceInfo(StringFilter deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public StringFilter getRefreshToken() {
        return refreshToken;
    }

    public Optional<StringFilter> optionalRefreshToken() {
        return Optional.ofNullable(refreshToken);
    }

    public StringFilter refreshToken() {
        if (refreshToken == null) {
            setRefreshToken(new StringFilter());
        }
        return refreshToken;
    }

    public void setRefreshToken(StringFilter refreshToken) {
        this.refreshToken = refreshToken;
    }

    public InstantFilter getExpiresAt() {
        return expiresAt;
    }

    public Optional<InstantFilter> optionalExpiresAt() {
        return Optional.ofNullable(expiresAt);
    }

    public InstantFilter expiresAt() {
        if (expiresAt == null) {
            setExpiresAt(new InstantFilter());
        }
        return expiresAt;
    }

    public void setExpiresAt(InstantFilter expiresAt) {
        this.expiresAt = expiresAt;
    }

    public BooleanFilter getIsRevoked() {
        return isRevoked;
    }

    public Optional<BooleanFilter> optionalIsRevoked() {
        return Optional.ofNullable(isRevoked);
    }

    public BooleanFilter isRevoked() {
        if (isRevoked == null) {
            setIsRevoked(new BooleanFilter());
        }
        return isRevoked;
    }

    public void setIsRevoked(BooleanFilter isRevoked) {
        this.isRevoked = isRevoked;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public StringFilter getRefreshTokenHash() {
        return refreshTokenHash;
    }

    public Optional<StringFilter> optionalRefreshTokenHash() {
        return Optional.ofNullable(refreshTokenHash);
    }

    public StringFilter refreshTokenHash() {
        if (refreshTokenHash == null) {
            setRefreshTokenHash(new StringFilter());
        }
        return refreshTokenHash;
    }

    public void setRefreshTokenHash(StringFilter refreshTokenHash) {
        this.refreshTokenHash = refreshTokenHash;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AuthSessionCriteria that = (AuthSessionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(deviceInfo, that.deviceInfo) &&
            Objects.equals(refreshToken, that.refreshToken) &&
            Objects.equals(expiresAt, that.expiresAt) &&
            Objects.equals(isRevoked, that.isRevoked) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(refreshTokenHash, that.refreshTokenHash) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, deviceInfo, refreshToken, expiresAt, isRevoked, createdAt, refreshTokenHash, userId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuthSessionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalDeviceInfo().map(f -> "deviceInfo=" + f + ", ").orElse("") +
            optionalRefreshToken().map(f -> "refreshToken=" + f + ", ").orElse("") +
            optionalExpiresAt().map(f -> "expiresAt=" + f + ", ").orElse("") +
            optionalIsRevoked().map(f -> "isRevoked=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalRefreshTokenHash().map(f -> "refreshTokenHash=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
