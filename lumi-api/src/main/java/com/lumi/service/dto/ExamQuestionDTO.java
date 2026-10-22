package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.ExamQuestion} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExamQuestionDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 0)
    private Integer orderIndex;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal score;

    @NotNull
    private ExamDTO exam;

    @NotNull
    private QuestionDTO question;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public ExamDTO getExam() {
        return exam;
    }

    public void setExam(ExamDTO exam) {
        this.exam = exam;
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
        if (!(o instanceof ExamQuestionDTO)) {
            return false;
        }

        ExamQuestionDTO examQuestionDTO = (ExamQuestionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, examQuestionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExamQuestionDTO{" +
            "id=" + getId() +
            ", orderIndex=" + getOrderIndex() +
            ", score=" + getScore() +
            ", exam=" + getExam() +
            ", question=" + getQuestion() +
            "}";
    }
}
