package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.ProctorEvent} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProctorEventDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String violationType;

    @Size(max = 1000)
    private String photoUrl;

    private Instant occurredAt;

    @NotNull
    private ProctorSessionDTO session;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getViolationType() {
        return violationType;
    }

    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public ProctorSessionDTO getSession() {
        return session;
    }

    public void setSession(ProctorSessionDTO session) {
        this.session = session;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProctorEventDTO)) {
            return false;
        }

        ProctorEventDTO proctorEventDTO = (ProctorEventDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, proctorEventDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProctorEventDTO{" +
            "id=" + getId() +
            ", violationType='" + getViolationType() + "'" +
            ", photoUrl='" + getPhotoUrl() + "'" +
            ", occurredAt='" + getOccurredAt() + "'" +
            ", session=" + getSession() +
            "}";
    }
}
