package com.lumi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Lưu báo sai nội dung, đối tượng bị báo và kết quả xử lý.
 */
@Entity
@Table(name = "content_reports")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ContentReport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "target_type", length = 50, nullable = false)
    private String targetType;

    @NotNull
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "reason", nullable = false, columnDefinition = "text")
    private String reason;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "resolution_note", columnDefinition = "text")
    private String resolutionNote;

    @Size(max = 100)
    @Column(name = "target_version", length = 100)
    private String targetVersion;

    @ManyToOne(optional = false)
    @NotNull
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    private User resolvedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ContentReport id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetType() {
        return this.targetType;
    }

    public ContentReport targetType(String targetType) {
        this.setTargetType(targetType);
        return this;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getTargetId() {
        return this.targetId;
    }

    public ContentReport targetId(Long targetId) {
        this.setTargetId(targetId);
        return this;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getReason() {
        return this.reason;
    }

    public ContentReport reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return this.status;
    }

    public ContentReport status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public ContentReport createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getResolvedAt() {
        return this.resolvedAt;
    }

    public ContentReport resolvedAt(Instant resolvedAt) {
        this.setResolvedAt(resolvedAt);
        return this;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getResolutionNote() {
        return this.resolutionNote;
    }

    public ContentReport resolutionNote(String resolutionNote) {
        this.setResolutionNote(resolutionNote);
        return this;
    }

    public void setResolutionNote(String resolutionNote) {
        this.resolutionNote = resolutionNote;
    }

    public String getTargetVersion() {
        return this.targetVersion;
    }

    public ContentReport targetVersion(String targetVersion) {
        this.setTargetVersion(targetVersion);
        return this;
    }

    public void setTargetVersion(String targetVersion) {
        this.targetVersion = targetVersion;
    }

    public User getReporter() {
        return this.reporter;
    }

    public void setReporter(User user) {
        this.reporter = user;
    }

    public ContentReport reporter(User user) {
        this.setReporter(user);
        return this;
    }

    public User getAssignedTo() {
        return this.assignedTo;
    }

    public void setAssignedTo(User user) {
        this.assignedTo = user;
    }

    public ContentReport assignedTo(User user) {
        this.setAssignedTo(user);
        return this;
    }

    public User getResolvedBy() {
        return this.resolvedBy;
    }

    public void setResolvedBy(User user) {
        this.resolvedBy = user;
    }

    public ContentReport resolvedBy(User user) {
        this.setResolvedBy(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContentReport)) {
            return false;
        }
        return getId() != null && getId().equals(((ContentReport) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ContentReport{" +
            "id=" + getId() +
            ", targetType='" + getTargetType() + "'" +
            ", targetId=" + getTargetId() +
            ", reason='" + getReason() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", resolvedAt='" + getResolvedAt() + "'" +
            ", resolutionNote='" + getResolutionNote() + "'" +
            ", targetVersion='" + getTargetVersion() + "'" +
            "}";
    }
}
