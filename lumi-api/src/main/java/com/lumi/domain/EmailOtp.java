package com.lumi.domain;

import com.lumi.domain.enumeration.OtpPurpose;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * An EmailOtp entity for storing OTPs sent to users.
 */
@Entity
@Table(name = "email_otp")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EmailOtp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @NotNull
    @Size(max = 10)
    @Column(name = "otp_code", length = 10, nullable = false)
    private String otpCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", length = 50, nullable = false)
    private OtpPurpose purpose;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @NotNull
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Size(max = 45)
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    public Long getId() {
        return this.id;
    }

    public EmailOtp id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public EmailOtp email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtpCode() {
        return this.otpCode;
    }

    public EmailOtp otpCode(String otpCode) {
        this.setOtpCode(otpCode);
        return this;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public OtpPurpose getPurpose() {
        return this.purpose;
    }

    public EmailOtp purpose(OtpPurpose purpose) {
        this.setPurpose(purpose);
        return this;
    }

    public void setPurpose(OtpPurpose purpose) {
        this.purpose = purpose;
    }

    public Instant getExpiresAt() {
        return this.expiresAt;
    }

    public EmailOtp expiresAt(Instant expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getIsUsed() {
        return this.isUsed;
    }

    public EmailOtp isUsed(Boolean isUsed) {
        this.setIsUsed(isUsed);
        return this;
    }

    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }

    public Instant getVerifiedAt() {
        return this.verifiedAt;
    }

    public EmailOtp verifiedAt(Instant verifiedAt) {
        this.setVerifiedAt(verifiedAt);
        return this;
    }

    public void setVerifiedAt(Instant verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public EmailOtp createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public EmailOtp ipAddress(String ipAddress) {
        this.setIpAddress(ipAddress);
        return this;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EmailOtp)) {
            return false;
        }
        return getId() != null && getId().equals(((EmailOtp) o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return (
            "EmailOtp{" +
            "id=" +
            getId() +
            ", email='" +
            getEmail() +
            "'" +
            ", purpose='" +
            getPurpose() +
            "'" +
            ", expiresAt='" +
            getExpiresAt() +
            "'" +
            ", isUsed='" +
            getIsUsed() +
            "'" +
            ", verifiedAt='" +
            getVerifiedAt() +
            "'" +
            ", createdAt='" +
            getCreatedAt() +
            "'" +
            ", ipAddress='" +
            getIpAddress() +
            "'" +
            "}"
        );
    }
}
