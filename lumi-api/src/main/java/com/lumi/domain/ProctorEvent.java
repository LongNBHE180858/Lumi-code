package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * A ProctorEvent.
 */
@Entity
@Table(name = "proctor_event")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProctorEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "violation_type", length = 50, nullable = false)
    private String violationType;

    @Size(max = 1000)
    @Column(name = "photo_url", length = 1000)
    private String photoUrl;

    @Column(name = "occurred_at")
    private Instant occurredAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "attempt" }, allowSetters = true)
    private ProctorSession session;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProctorEvent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getViolationType() {
        return this.violationType;
    }

    public ProctorEvent violationType(String violationType) {
        this.setViolationType(violationType);
        return this;
    }

    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }

    public String getPhotoUrl() {
        return this.photoUrl;
    }

    public ProctorEvent photoUrl(String photoUrl) {
        this.setPhotoUrl(photoUrl);
        return this;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Instant getOccurredAt() {
        return this.occurredAt;
    }

    public ProctorEvent occurredAt(Instant occurredAt) {
        this.setOccurredAt(occurredAt);
        return this;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public ProctorSession getSession() {
        return this.session;
    }

    public void setSession(ProctorSession proctorSession) {
        this.session = proctorSession;
    }

    public ProctorEvent session(ProctorSession proctorSession) {
        this.setSession(proctorSession);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProctorEvent)) {
            return false;
        }
        return getId() != null && getId().equals(((ProctorEvent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProctorEvent{" +
            "id=" + getId() +
            ", violationType='" + getViolationType() + "'" +
            ", photoUrl='" + getPhotoUrl() + "'" +
            ", occurredAt='" + getOccurredAt() + "'" +
            "}";
    }
}
