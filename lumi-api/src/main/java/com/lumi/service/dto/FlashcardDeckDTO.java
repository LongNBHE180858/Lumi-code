package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.FlashcardDeck} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FlashcardDeckDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String title;

    private Boolean isPublic;

    @NotNull
    private UserDTO owner;

    private SubjectDTO subject;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public UserDTO getOwner() {
        return owner;
    }

    public void setOwner(UserDTO owner) {
        this.owner = owner;
    }

    public SubjectDTO getSubject() {
        return subject;
    }

    public void setSubject(SubjectDTO subject) {
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FlashcardDeckDTO)) {
            return false;
        }

        FlashcardDeckDTO flashcardDeckDTO = (FlashcardDeckDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, flashcardDeckDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FlashcardDeckDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", isPublic='" + getIsPublic() + "'" +
            ", owner=" + getOwner() +
            ", subject=" + getSubject() +
            "}";
    }
}
