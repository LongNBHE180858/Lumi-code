package com.lumi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * A AuditLog.
 */
@Entity
@Table(name = "audit_log")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "action", length = 100, nullable = false)
    private String action;

    @NotNull
    @Size(max = 50)
    @Column(name = "target_entity", length = 50, nullable = false)
    private String targetEntity;

    @NotNull
    @Size(max = 100)
    @Column(name = "target_id", length = 100, nullable = false)
    private String targetId;

    @Size(max = 50)
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Size(max = 5000)
    @Column(name = "details", length = 5000)
    private String details;

    @Column(name = "occurred_at")
    private Instant occurredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User actor;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AuditLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return this.action;
    }

    public AuditLog action(String action) {
        this.setAction(action);
        return this;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTargetEntity() {
        return this.targetEntity;
    }

    public AuditLog targetEntity(String targetEntity) {
        this.setTargetEntity(targetEntity);
        return this;
    }

    public void setTargetEntity(String targetEntity) {
        this.targetEntity = targetEntity;
    }

    public String getTargetId() {
        return this.targetId;
    }

    public AuditLog targetId(String targetId) {
        this.setTargetId(targetId);
        return this;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public AuditLog ipAddress(String ipAddress) {
        this.setIpAddress(ipAddress);
        return this;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDetails() {
        return this.details;
    }

    public AuditLog details(String details) {
        this.setDetails(details);
        return this;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Instant getOccurredAt() {
        return this.occurredAt;
    }

    public AuditLog occurredAt(Instant occurredAt) {
        this.setOccurredAt(occurredAt);
        return this;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public User getActor() {
        return this.actor;
    }

    public void setActor(User user) {
        this.actor = user;
    }

    public AuditLog actor(User user) {
        this.setActor(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditLog)) {
            return false;
        }
        return getId() != null && getId().equals(((AuditLog) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditLog{" +
            "id=" + getId() +
            ", action='" + getAction() + "'" +
            ", targetEntity='" + getTargetEntity() + "'" +
            ", targetId='" + getTargetId() + "'" +
            ", ipAddress='" + getIpAddress() + "'" +
            ", details='" + getDetails() + "'" +
            ", occurredAt='" + getOccurredAt() + "'" +
            "}";
    }
}
