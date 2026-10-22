package com.lumi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * A ParentStudentLink.
 */
@Entity
@Table(name = "parent_student_link")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParentStudentLink implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 10)
    @Column(name = "link_code", length = 10, nullable = false, unique = true)
    private String linkCode;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "linked_at")
    private Instant linkedAt;

    @ManyToOne(optional = false)
    @NotNull
    private User parent;

    @ManyToOne(optional = false)
    @NotNull
    private User student;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ParentStudentLink id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLinkCode() {
        return this.linkCode;
    }

    public ParentStudentLink linkCode(String linkCode) {
        this.setLinkCode(linkCode);
        return this;
    }

    public void setLinkCode(String linkCode) {
        this.linkCode = linkCode;
    }

    public String getStatus() {
        return this.status;
    }

    public ParentStudentLink status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getLinkedAt() {
        return this.linkedAt;
    }

    public ParentStudentLink linkedAt(Instant linkedAt) {
        this.setLinkedAt(linkedAt);
        return this;
    }

    public void setLinkedAt(Instant linkedAt) {
        this.linkedAt = linkedAt;
    }

    public User getParent() {
        return this.parent;
    }

    public void setParent(User user) {
        this.parent = user;
    }

    public ParentStudentLink parent(User user) {
        this.setParent(user);
        return this;
    }

    public User getStudent() {
        return this.student;
    }

    public void setStudent(User user) {
        this.student = user;
    }

    public ParentStudentLink student(User user) {
        this.setStudent(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParentStudentLink)) {
            return false;
        }
        return getId() != null && getId().equals(((ParentStudentLink) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParentStudentLink{" +
            "id=" + getId() +
            ", linkCode='" + getLinkCode() + "'" +
            ", status='" + getStatus() + "'" +
            ", linkedAt='" + getLinkedAt() + "'" +
            "}";
    }
}
