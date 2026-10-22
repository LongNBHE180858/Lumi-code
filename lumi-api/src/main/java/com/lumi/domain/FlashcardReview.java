package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A FlashcardReview.
 */
@Entity
@Table(name = "flashcard_review")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FlashcardReview implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "next_review_at")
    private Instant nextReviewAt;

    @DecimalMin(value = "0")
    @Column(name = "interval_days", precision = 21, scale = 2)
    private BigDecimal intervalDays;

    @ManyToOne(optional = false)
    @NotNull
    private User student;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "deck" }, allowSetters = true)
    private Flashcard flashcard;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FlashcardReview id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getNextReviewAt() {
        return this.nextReviewAt;
    }

    public FlashcardReview nextReviewAt(Instant nextReviewAt) {
        this.setNextReviewAt(nextReviewAt);
        return this;
    }

    public void setNextReviewAt(Instant nextReviewAt) {
        this.nextReviewAt = nextReviewAt;
    }

    public BigDecimal getIntervalDays() {
        return this.intervalDays;
    }

    public FlashcardReview intervalDays(BigDecimal intervalDays) {
        this.setIntervalDays(intervalDays);
        return this;
    }

    public void setIntervalDays(BigDecimal intervalDays) {
        this.intervalDays = intervalDays;
    }

    public User getStudent() {
        return this.student;
    }

    public void setStudent(User user) {
        this.student = user;
    }

    public FlashcardReview student(User user) {
        this.setStudent(user);
        return this;
    }

    public Flashcard getFlashcard() {
        return this.flashcard;
    }

    public void setFlashcard(Flashcard flashcard) {
        this.flashcard = flashcard;
    }

    public FlashcardReview flashcard(Flashcard flashcard) {
        this.setFlashcard(flashcard);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FlashcardReview)) {
            return false;
        }
        return getId() != null && getId().equals(((FlashcardReview) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FlashcardReview{" +
            "id=" + getId() +
            ", nextReviewAt='" + getNextReviewAt() + "'" +
            ", intervalDays=" + getIntervalDays() +
            "}";
    }
}
