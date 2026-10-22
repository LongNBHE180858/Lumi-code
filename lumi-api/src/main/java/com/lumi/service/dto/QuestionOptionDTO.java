package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.QuestionOption} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuestionOptionDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 5000)
    private String content;

    private Boolean isCorrect;

    @NotNull
    private QuestionDTO question;

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

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public QuestionDTO getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDTO question) {
        this.question = question;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuestionOptionDTO)) {
            return false;
        }

        QuestionOptionDTO questionOptionDTO = (QuestionOptionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, questionOptionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuestionOptionDTO{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", isCorrect='" + getIsCorrect() + "'" +
            ", question=" + getQuestion() +
            "}";
    }
}
