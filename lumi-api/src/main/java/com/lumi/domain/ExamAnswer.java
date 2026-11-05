package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Lưu câu trả lời và điểm của học sinh cho từng câu trong một lượt làm.
 */
@Entity
@Table(name = "exam_answers")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExamAnswer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "text_answer", columnDefinition = "text")
    private String textAnswer;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "score", precision = 21, scale = 2)
    private BigDecimal score;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @NotNull
    @Column(name = "row_version", nullable = false)
    private Long rowVersion;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "exam", "student", "assignment" }, allowSetters = true)
    private ExamAttempt attempt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "bank", "createdBy", "aiContent", "subject", "grade", "previousQuestion" }, allowSetters = true)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "question" }, allowSetters = true)
    private QuestionOption option;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ExamAnswer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTextAnswer() {
        return this.textAnswer;
    }

    public ExamAnswer textAnswer(String textAnswer) {
        this.setTextAnswer(textAnswer);
        return this;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }

    public Boolean getIsCorrect() {
        return this.isCorrect;
    }

    public ExamAnswer isCorrect(Boolean isCorrect) {
        this.setIsCorrect(isCorrect);
        return this;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public BigDecimal getScore() {
        return this.score;
    }

    public ExamAnswer score(BigDecimal score) {
        this.setScore(score);
        return this;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public ExamAnswer updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getRowVersion() {
        return this.rowVersion;
    }

    public ExamAnswer rowVersion(Long rowVersion) {
        this.setRowVersion(rowVersion);
        return this;
    }

    public void setRowVersion(Long rowVersion) {
        this.rowVersion = rowVersion;
    }

    public ExamAttempt getAttempt() {
        return this.attempt;
    }

    public void setAttempt(ExamAttempt examAttempt) {
        this.attempt = examAttempt;
    }

    public ExamAnswer attempt(ExamAttempt examAttempt) {
        this.setAttempt(examAttempt);
        return this;
    }

    public Question getQuestion() {
        return this.question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public ExamAnswer question(Question question) {
        this.setQuestion(question);
        return this;
    }

    public QuestionOption getOption() {
        return this.option;
    }

    public void setOption(QuestionOption questionOption) {
        this.option = questionOption;
    }

    public ExamAnswer option(QuestionOption questionOption) {
        this.setOption(questionOption);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExamAnswer)) {
            return false;
        }
        return getId() != null && getId().equals(((ExamAnswer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExamAnswer{" +
            "id=" + getId() +
            ", textAnswer='" + getTextAnswer() + "'" +
            ", isCorrect='" + getIsCorrect() + "'" +
            ", score=" + getScore() +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", rowVersion=" + getRowVersion() +
            "}";
    }
}
