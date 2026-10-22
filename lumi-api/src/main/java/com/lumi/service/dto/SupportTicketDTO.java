package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.SupportTicket} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SupportTicketDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String type;

    @NotNull
    @Size(max = 255)
    private String subject;

    @Size(max = 20)
    private String status;

    private Instant createdAt;

    @NotNull
    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SupportTicketDTO)) {
            return false;
        }

        SupportTicketDTO supportTicketDTO = (SupportTicketDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, supportTicketDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SupportTicketDTO{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", subject='" + getSubject() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
