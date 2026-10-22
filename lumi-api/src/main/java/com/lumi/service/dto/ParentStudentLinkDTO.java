package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.ParentStudentLink} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParentStudentLinkDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 10)
    private String linkCode;

    @Size(max = 20)
    private String status;

    private Instant linkedAt;

    @NotNull
    private UserDTO parent;

    @NotNull
    private UserDTO student;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLinkCode() {
        return linkCode;
    }

    public void setLinkCode(String linkCode) {
        this.linkCode = linkCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getLinkedAt() {
        return linkedAt;
    }

    public void setLinkedAt(Instant linkedAt) {
        this.linkedAt = linkedAt;
    }

    public UserDTO getParent() {
        return parent;
    }

    public void setParent(UserDTO parent) {
        this.parent = parent;
    }

    public UserDTO getStudent() {
        return student;
    }

    public void setStudent(UserDTO student) {
        this.student = student;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParentStudentLinkDTO)) {
            return false;
        }

        ParentStudentLinkDTO parentStudentLinkDTO = (ParentStudentLinkDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, parentStudentLinkDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParentStudentLinkDTO{" +
            "id=" + getId() +
            ", linkCode='" + getLinkCode() + "'" +
            ", status='" + getStatus() + "'" +
            ", linkedAt='" + getLinkedAt() + "'" +
            ", parent=" + getParent() +
            ", student=" + getStudent() +
            "}";
    }
}
