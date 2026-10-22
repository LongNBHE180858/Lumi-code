package com.lumi.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumi.domain.Flashcard} entity. This class is used
 * in {@link com.lumi.web.rest.FlashcardResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /flashcards?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FlashcardCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter frontText;

    private StringFilter backText;

    private LongFilter deckId;

    private Boolean distinct;

    public FlashcardCriteria() {}

    public FlashcardCriteria(FlashcardCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.frontText = other.optionalFrontText().map(StringFilter::copy).orElse(null);
        this.backText = other.optionalBackText().map(StringFilter::copy).orElse(null);
        this.deckId = other.optionalDeckId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public FlashcardCriteria copy() {
        return new FlashcardCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getFrontText() {
        return frontText;
    }

    public Optional<StringFilter> optionalFrontText() {
        return Optional.ofNullable(frontText);
    }

    public StringFilter frontText() {
        if (frontText == null) {
            setFrontText(new StringFilter());
        }
        return frontText;
    }

    public void setFrontText(StringFilter frontText) {
        this.frontText = frontText;
    }

    public StringFilter getBackText() {
        return backText;
    }

    public Optional<StringFilter> optionalBackText() {
        return Optional.ofNullable(backText);
    }

    public StringFilter backText() {
        if (backText == null) {
            setBackText(new StringFilter());
        }
        return backText;
    }

    public void setBackText(StringFilter backText) {
        this.backText = backText;
    }

    public LongFilter getDeckId() {
        return deckId;
    }

    public Optional<LongFilter> optionalDeckId() {
        return Optional.ofNullable(deckId);
    }

    public LongFilter deckId() {
        if (deckId == null) {
            setDeckId(new LongFilter());
        }
        return deckId;
    }

    public void setDeckId(LongFilter deckId) {
        this.deckId = deckId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FlashcardCriteria that = (FlashcardCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(frontText, that.frontText) &&
            Objects.equals(backText, that.backText) &&
            Objects.equals(deckId, that.deckId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, frontText, backText, deckId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FlashcardCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalFrontText().map(f -> "frontText=" + f + ", ").orElse("") +
            optionalBackText().map(f -> "backText=" + f + ", ").orElse("") +
            optionalDeckId().map(f -> "deckId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
