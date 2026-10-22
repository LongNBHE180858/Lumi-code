package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.Flashcard} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FlashcardDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 5000)
    private String frontText;

    @NotNull
    @Size(max = 5000)
    private String backText;

    @NotNull
    private FlashcardDeckDTO deck;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFrontText() {
        return frontText;
    }

    public void setFrontText(String frontText) {
        this.frontText = frontText;
    }

    public String getBackText() {
        return backText;
    }

    public void setBackText(String backText) {
        this.backText = backText;
    }

    public FlashcardDeckDTO getDeck() {
        return deck;
    }

    public void setDeck(FlashcardDeckDTO deck) {
        this.deck = deck;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FlashcardDTO)) {
            return false;
        }

        FlashcardDTO flashcardDTO = (FlashcardDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, flashcardDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FlashcardDTO{" +
            "id=" + getId() +
            ", frontText='" + getFrontText() + "'" +
            ", backText='" + getBackText() + "'" +
            ", deck=" + getDeck() +
            "}";
    }
}
