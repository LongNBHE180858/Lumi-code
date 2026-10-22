package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.FlashcardReview} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FlashcardReviewDTO implements Serializable {

    private Long id;

    private Instant nextReviewAt;

    @DecimalMin(value = "0")
    private BigDecimal intervalDays;

    @NotNull
    private UserDTO student;

    @NotNull
    private FlashcardDTO flashcard;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getNextReviewAt() {
        return nextReviewAt;
    }

    public void setNextReviewAt(Instant nextReviewAt) {
        this.nextReviewAt = nextReviewAt;
    }

    public BigDecimal getIntervalDays() {
        return intervalDays;
    }

    public void setIntervalDays(BigDecimal intervalDays) {
        this.intervalDays = intervalDays;
    }

    public UserDTO getStudent() {
        return student;
    }

    public void setStudent(UserDTO student) {
        this.student = student;
    }

    public FlashcardDTO getFlashcard() {
        return flashcard;
    }

    public void setFlashcard(FlashcardDTO flashcard) {
        this.flashcard = flashcard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FlashcardReviewDTO)) {
            return false;
        }

        FlashcardReviewDTO flashcardReviewDTO = (FlashcardReviewDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, flashcardReviewDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FlashcardReviewDTO{" +
            "id=" + getId() +
            ", nextReviewAt='" + getNextReviewAt() + "'" +
            ", intervalDays=" + getIntervalDays() +
            ", student=" + getStudent() +
            ", flashcard=" + getFlashcard() +
            "}";
    }
}
