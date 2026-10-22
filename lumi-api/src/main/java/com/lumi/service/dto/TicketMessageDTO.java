package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.TicketMessage} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketMessageDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 5000)
    private String content;

    private Instant createdAt;

    @NotNull
    private UserDTO sender;

    @NotNull
    private SupportTicketDTO ticket;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UserDTO getSender() {
        return sender;
    }

    public void setSender(UserDTO sender) {
        this.sender = sender;
    }

    public SupportTicketDTO getTicket() {
        return ticket;
    }

    public void setTicket(SupportTicketDTO ticket) {
        this.ticket = ticket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TicketMessageDTO)) {
            return false;
        }

        TicketMessageDTO ticketMessageDTO = (TicketMessageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ticketMessageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketMessageDTO{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", sender=" + getSender() +
            ", ticket=" + getTicket() +
            "}";
    }
}
