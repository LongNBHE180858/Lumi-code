package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.QuestionKnowledgeLink} entity.
 */
@Schema(description = "Lưu liên kết và trọng số giữa câu hỏi với đơn vị kiến thức.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuestionKnowledgeLinkDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal weight;

    @NotNull
    private QuestionDTO question;

    @NotNull
    private KnowledgeNodeDTO knowledgeNode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public QuestionDTO getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDTO question) {
        this.question = question;
    }

    public KnowledgeNodeDTO getKnowledgeNode() {
        return knowledgeNode;
    }

    public void setKnowledgeNode(KnowledgeNodeDTO knowledgeNode) {
        this.knowledgeNode = knowledgeNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuestionKnowledgeLinkDTO)) {
            return false;
        }

        QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO = (QuestionKnowledgeLinkDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, questionKnowledgeLinkDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuestionKnowledgeLinkDTO{" +
            "id=" + getId() +
            ", weight=" + getWeight() +
            ", question=" + getQuestion() +
            ", knowledgeNode=" + getKnowledgeNode() +
            "}";
    }
}
