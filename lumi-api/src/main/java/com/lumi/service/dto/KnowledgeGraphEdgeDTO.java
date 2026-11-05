package com.lumi.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO representing a directed prerequisite relationship in the Knowledge Graph.
 * fromNodeId is the prerequisite of toNodeId (fromNode -> toNode).
 */
public class KnowledgeGraphEdgeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String RELATION_TYPE_PREREQUISITE = "PREREQUISITE";

    private Long id;

    @NotNull
    private Long fromNodeId;

    @NotNull
    private Long toNodeId;

    @Size(max = 50)
    private String relationType = RELATION_TYPE_PREREQUISITE;

    public KnowledgeGraphEdgeDTO() {}

    public KnowledgeGraphEdgeDTO(Long id, Long fromNodeId, Long toNodeId, String relationType) {
        this.id = id;
        this.fromNodeId = fromNodeId;
        this.toNodeId = toNodeId;
        this.relationType = relationType != null ? relationType : RELATION_TYPE_PREREQUISITE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromNodeId() {
        return fromNodeId;
    }

    public void setFromNodeId(Long fromNodeId) {
        this.fromNodeId = fromNodeId;
    }

    public Long getToNodeId() {
        return toNodeId;
    }

    public void setToNodeId(Long toNodeId) {
        this.toNodeId = toNodeId;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KnowledgeGraphEdgeDTO that = (KnowledgeGraphEdgeDTO) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(fromNodeId, that.fromNodeId) &&
            Objects.equals(toNodeId, that.toNodeId) &&
            Objects.equals(relationType, that.relationType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fromNodeId, toNodeId, relationType);
    }

    @Override
    public String toString() {
        return "KnowledgeGraphEdgeDTO{" +
            "id=" + id +
            ", fromNodeId=" + fromNodeId +
            ", toNodeId=" + toNodeId +
            ", relationType='" + relationType + '\'' +
            '}';
    }
}
