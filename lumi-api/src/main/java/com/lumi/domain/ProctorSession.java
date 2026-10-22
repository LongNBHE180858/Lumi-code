package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * A ProctorSession.
 */
@Entity
@Table(name = "proctor_session")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProctorSession implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "face_verified")
    private Boolean faceVerified;

    @Min(value = 0)
    @Column(name = "warning_count")
    private Integer warningCount;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;

    @JsonIgnoreProperties(value = { "student", "exam", "proctorSession" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private ExamAttempt attempt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProctorSession id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getFaceVerified() {
        return this.faceVerified;
    }

    public ProctorSession faceVerified(Boolean faceVerified) {
        this.setFaceVerified(faceVerified);
        return this;
    }

    public void setFaceVerified(Boolean faceVerified) {
        this.faceVerified = faceVerified;
    }

    public Integer getWarningCount() {
        return this.warningCount;
    }

    public ProctorSession warningCount(Integer warningCount) {
        this.setWarningCount(warningCount);
        return this;
    }

    public void setWarningCount(Integer warningCount) {
        this.warningCount = warningCount;
    }

    public String getStatus() {
        return this.status;
    }

    public ProctorSession status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ExamAttempt getAttempt() {
        return this.attempt;
    }

    public void setAttempt(ExamAttempt examAttempt) {
        this.attempt = examAttempt;
    }

    public ProctorSession attempt(ExamAttempt examAttempt) {
        this.setAttempt(examAttempt);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProctorSession)) {
            return false;
        }
        return getId() != null && getId().equals(((ProctorSession) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProctorSession{" +
            "id=" + getId() +
            ", faceVerified='" + getFaceVerified() + "'" +
            ", warningCount=" + getWarningCount() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
