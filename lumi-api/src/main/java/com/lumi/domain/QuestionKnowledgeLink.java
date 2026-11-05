package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Lưu liên kết và trọng số giữa câu hỏi với đơn vị kiến thức.
 */
@Entity
@Table(name = "question_knowledge_links")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuestionKnowledgeLink implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "weight", precision = 21, scale = 2, nullable = false)
    private BigDecimal weight;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "bank", "createdBy", "aiContent", "subject", "grade", "previousQuestion" }, allowSetters = true)
    private Question question;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "subject" }, allowSetters = true)
    private KnowledgeNode knowledgeNode;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public QuestionKnowledgeLink id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getWeight() {
        return this.weight;
    }

    public QuestionKnowledgeLink weight(BigDecimal weight) {
        this.setWeight(weight);
        return this;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Question getQuestion() {
        return this.question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public QuestionKnowledgeLink question(Question question) {
        this.setQuestion(question);
        return this;
    }

    public KnowledgeNode getKnowledgeNode() {
        return this.knowledgeNode;
    }

    public void setKnowledgeNode(KnowledgeNode knowledgeNode) {
        this.knowledgeNode = knowledgeNode;
    }

    public QuestionKnowledgeLink knowledgeNode(KnowledgeNode knowledgeNode) {
        this.setKnowledgeNode(knowledgeNode);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuestionKnowledgeLink)) {
            return false;
        }
        return getId() != null && getId().equals(((QuestionKnowledgeLink) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuestionKnowledgeLink{" +
            "id=" + getId() +
            ", weight=" + getWeight() +
            "}";
    }
}
