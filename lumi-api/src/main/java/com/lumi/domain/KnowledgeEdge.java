package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * A KnowledgeEdge.
 */
@Entity
@Table(name = "knowledge_edge")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class KnowledgeEdge implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "relation_type", length = 50, nullable = false)
    private String relationType;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "subject" }, allowSetters = true)
    private KnowledgeNode fromNode;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "subject" }, allowSetters = true)
    private KnowledgeNode toNode;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public KnowledgeEdge id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRelationType() {
        return this.relationType;
    }

    public KnowledgeEdge relationType(String relationType) {
        this.setRelationType(relationType);
        return this;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public KnowledgeNode getFromNode() {
        return this.fromNode;
    }

    public void setFromNode(KnowledgeNode knowledgeNode) {
        this.fromNode = knowledgeNode;
    }

    public KnowledgeEdge fromNode(KnowledgeNode knowledgeNode) {
        this.setFromNode(knowledgeNode);
        return this;
    }

    public KnowledgeNode getToNode() {
        return this.toNode;
    }

    public void setToNode(KnowledgeNode knowledgeNode) {
        this.toNode = knowledgeNode;
    }

    public KnowledgeEdge toNode(KnowledgeNode knowledgeNode) {
        this.setToNode(knowledgeNode);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KnowledgeEdge)) {
            return false;
        }
        return getId() != null && getId().equals(((KnowledgeEdge) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KnowledgeEdge{" +
            "id=" + getId() +
            ", relationType='" + getRelationType() + "'" +
            "}";
    }
}
