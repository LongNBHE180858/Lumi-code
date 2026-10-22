package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.Question} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuestionDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 5000)
    private String content;

    @NotNull
    @Size(max = 20)
    private String type;

    @Min(value = 1)
    @Max(value = 5)
    private Integer difficulty;

    @Size(max = 5000)
    private String explanation;

    private Boolean isDeleted;

    private QuestionBankDTO bank;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public QuestionBankDTO getBank() {
        return bank;
    }

    public void setBank(QuestionBankDTO bank) {
        this.bank = bank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuestionDTO)) {
            return false;
        }

        QuestionDTO questionDTO = (QuestionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, questionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuestionDTO{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", type='" + getType() + "'" +
            ", difficulty=" + getDifficulty() +
            ", explanation='" + getExplanation() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", bank=" + getBank() +
            "}";
    }
}
