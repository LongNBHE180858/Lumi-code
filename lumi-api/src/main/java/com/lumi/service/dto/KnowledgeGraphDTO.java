package com.lumi.service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A DTO representing a complete Knowledge Graph candidate or persisted view for a single subject.
 */
public class KnowledgeGraphDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long subjectId;

    private String subjectName;

    private String subjectDescription;

    @Valid
    private List<KnowledgeGraphNodeDTO> nodes = new ArrayList<>();

    @Valid
    private List<KnowledgeGraphEdgeDTO> edges = new ArrayList<>();

    public KnowledgeGraphDTO() {}

    public KnowledgeGraphDTO(Long subjectId, String subjectName, String subjectDescription) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.subjectDescription = subjectDescription;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectDescription() {
        return subjectDescription;
    }

    public void setSubjectDescription(String subjectDescription) {
        this.subjectDescription = subjectDescription;
    }

    public List<KnowledgeGraphNodeDTO> getNodes() {
        return nodes;
    }

    public void setNodes(List<KnowledgeGraphNodeDTO> nodes) {
        this.nodes = nodes != null ? nodes : new ArrayList<>();
    }

    public List<KnowledgeGraphEdgeDTO> getEdges() {
        return edges;
    }

    public void setEdges(List<KnowledgeGraphEdgeDTO> edges) {
        this.edges = edges != null ? edges : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "KnowledgeGraphDTO{" +
            "subjectId=" + subjectId +
            ", subjectName='" + subjectName + '\'' +
            ", nodeCount=" + (nodes != null ? nodes.size() : 0) +
            ", edgeCount=" + (edges != null ? edges.size() : 0) +
            '}';
    }
}
