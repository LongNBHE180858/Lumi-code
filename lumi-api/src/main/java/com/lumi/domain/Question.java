package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * A Question.
 */
@Entity
@Table(name = "question")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Question implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 5000)
    @Column(name = "content", length = 5000, nullable = false)
    private String content;

    @NotNull
    @Size(max = 20)
    @Column(name = "type", length = 20, nullable = false)
    private String type;

    @Min(value = 1)
    @Max(value = 5)
    @Column(name = "difficulty")
    private Integer difficulty;

    @Size(max = 5000)
    @Column(name = "explanation", length = 5000)
    private String explanation;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "instructor" }, allowSetters = true)
    private QuestionBank bank;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Question id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public Question content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return this.type;
    }

    public Question type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDifficulty() {
        return this.difficulty;
    }

    public Question difficulty(Integer difficulty) {
        this.setDifficulty(difficulty);
        return this;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public String getExplanation() {
        return this.explanation;
    }

    public Question explanation(String explanation) {
        this.setExplanation(explanation);
        return this;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public Question isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public QuestionBank getBank() {
        return this.bank;
    }

    public void setBank(QuestionBank questionBank) {
        this.bank = questionBank;
    }

    public Question bank(QuestionBank questionBank) {
        this.setBank(questionBank);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Question)) {
            return false;
        }
        return getId() != null && getId().equals(((Question) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Question{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", type='" + getType() + "'" +
            ", difficulty=" + getDifficulty() +
            ", explanation='" + getExplanation() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            "}";
    }
}
