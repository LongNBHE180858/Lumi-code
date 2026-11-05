package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Lưu nội dung mặt trước, mặt sau và nguồn AI của từng thẻ.
 */
@Entity
@Table(name = "flashcards")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Flashcard implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "front_text", nullable = false, columnDefinition = "text")
    private String frontText;

    @Column(name = "back_text", nullable = false, columnDefinition = "text")
    private String backText;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "owner", "subject" }, allowSetters = true)
    private FlashcardDeck deck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "createdBy", "document", "lesson", "reviewedBy", "course" }, allowSetters = true)
    private AiGeneratedContent aiContent;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Flashcard id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFrontText() {
        return this.frontText;
    }

    public Flashcard frontText(String frontText) {
        this.setFrontText(frontText);
        return this;
    }

    public void setFrontText(String frontText) {
        this.frontText = frontText;
    }

    public String getBackText() {
        return this.backText;
    }

    public Flashcard backText(String backText) {
        this.setBackText(backText);
        return this;
    }

    public void setBackText(String backText) {
        this.backText = backText;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Flashcard updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public FlashcardDeck getDeck() {
        return this.deck;
    }

    public void setDeck(FlashcardDeck flashcardDeck) {
        this.deck = flashcardDeck;
    }

    public Flashcard deck(FlashcardDeck flashcardDeck) {
        this.setDeck(flashcardDeck);
        return this;
    }

    public AiGeneratedContent getAiContent() {
        return this.aiContent;
    }

    public void setAiContent(AiGeneratedContent aiGeneratedContent) {
        this.aiContent = aiGeneratedContent;
    }

    public Flashcard aiContent(AiGeneratedContent aiGeneratedContent) {
        this.setAiContent(aiGeneratedContent);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Flashcard)) {
            return false;
        }
        return getId() != null && getId().equals(((Flashcard) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Flashcard{" +
            "id=" + getId() +
            ", frontText='" + getFrontText() + "'" +
            ", backText='" + getBackText() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
