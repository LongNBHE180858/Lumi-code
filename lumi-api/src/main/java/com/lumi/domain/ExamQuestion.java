package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Lưu câu hỏi được chọn vào đề, thứ tự và điểm tối đa của câu.
 */
@Entity
@Table(name = "exam_questions")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExamQuestion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @NotNull
    @Column(name = "score", precision = 21, scale = 2, nullable = false)
    private BigDecimal score;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "studyClass", "createdBy", "lesson", "assignedStudent" }, allowSetters = true)
    private Exam exam;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "bank", "createdBy", "aiContent", "subject", "grade", "previousQuestion" }, allowSetters = true)
    private Question question;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ExamQuestion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrderIndex() {
        return this.orderIndex;
    }

    public ExamQuestion orderIndex(Integer orderIndex) {
        this.setOrderIndex(orderIndex);
        return this;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public BigDecimal getScore() {
        return this.score;
    }

    public ExamQuestion score(BigDecimal score) {
        this.setScore(score);
        return this;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Exam getExam() {
        return this.exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public ExamQuestion exam(Exam exam) {
        this.setExam(exam);
        return this;
    }

    public Question getQuestion() {
        return this.question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public ExamQuestion question(Question question) {
        this.setQuestion(question);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExamQuestion)) {
            return false;
        }
        return getId() != null && getId().equals(((ExamQuestion) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExamQuestion{" +
            "id=" + getId() +
            ", orderIndex=" + getOrderIndex() +
            ", score=" + getScore() +
            "}";
    }
}
