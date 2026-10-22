package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.ExamAnswer} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExamAnswerDTO implements Serializable {

    private Long id;

    @Size(max = 5000)
    private String textAnswer;

    private Boolean isCorrect;

    @DecimalMin(value = "0")
    private BigDecimal score;

    @NotNull
    private ExamAttemptDTO attempt;

    @NotNull
    private QuestionDTO question;

    private QuestionOptionDTO option;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public ExamAttemptDTO getAttempt() {
        return attempt;
    }

    public void setAttempt(ExamAttemptDTO attempt) {
        this.attempt = attempt;
    }

    public QuestionDTO getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDTO question) {
        this.question = question;
    }

    public QuestionOptionDTO getOption() {
        return option;
    }

    public void setOption(QuestionOptionDTO option) {
        this.option = option;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExamAnswerDTO)) {
            return false;
        }

        ExamAnswerDTO examAnswerDTO = (ExamAnswerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, examAnswerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExamAnswerDTO{" +
            "id=" + getId() +
            ", textAnswer='" + getTextAnswer() + "'" +
            ", isCorrect='" + getIsCorrect() + "'" +
            ", score=" + getScore() +
            ", attempt=" + getAttempt() +
            ", question=" + getQuestion() +
            ", option=" + getOption() +
            "}";
    }
}
