package com.lumi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * A FlashcardDeck.
 */
@Entity
@Table(name = "flashcard_deck")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FlashcardDeck implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "is_public")
    private Boolean isPublic;

    @ManyToOne(optional = false)
    @NotNull
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    private Subject subject;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FlashcardDeck id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public FlashcardDeck title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getIsPublic() {
        return this.isPublic;
    }

    public FlashcardDeck isPublic(Boolean isPublic) {
        this.setIsPublic(isPublic);
        return this;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public User getOwner() {
        return this.owner;
    }

    public void setOwner(User user) {
        this.owner = user;
    }

    public FlashcardDeck owner(User user) {
        this.setOwner(user);
        return this;
    }

    public Subject getSubject() {
        return this.subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public FlashcardDeck subject(Subject subject) {
        this.setSubject(subject);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FlashcardDeck)) {
            return false;
        }
        return getId() != null && getId().equals(((FlashcardDeck) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FlashcardDeck{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", isPublic='" + getIsPublic() + "'" +
            "}";
    }
}
