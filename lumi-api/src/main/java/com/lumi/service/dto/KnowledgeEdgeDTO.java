package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.KnowledgeEdge} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class KnowledgeEdgeDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String relationType;

    @NotNull
    private KnowledgeNodeDTO fromNode;

    @NotNull
    private KnowledgeNodeDTO toNode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public KnowledgeNodeDTO getFromNode() {
        return fromNode;
    }

    public void setFromNode(KnowledgeNodeDTO fromNode) {
        this.fromNode = fromNode;
    }

    public KnowledgeNodeDTO getToNode() {
        return toNode;
    }

    public void setToNode(KnowledgeNodeDTO toNode) {
        this.toNode = toNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KnowledgeEdgeDTO)) {
            return false;
        }

        KnowledgeEdgeDTO knowledgeEdgeDTO = (KnowledgeEdgeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, knowledgeEdgeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KnowledgeEdgeDTO{" +
            "id=" + getId() +
            ", relationType='" + getRelationType() + "'" +
            ", fromNode=" + getFromNode() +
            ", toNode=" + getToNode() +
            "}";
    }
}
