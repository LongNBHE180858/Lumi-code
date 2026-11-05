package com.lumi.service.builder;

import com.lumi.service.dto.KnowledgeGraphDTO;
import com.lumi.service.dto.KnowledgeGraphEdgeDTO;
import com.lumi.service.dto.KnowledgeGraphNodeDTO;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal Test Data Builder for constructing {@link KnowledgeGraphDTO} fixtures in unit tests.
 * Scope: Strictly within src/test/java.
 */
public class KnowledgeGraphDTOBuilder {

    private Long subjectId = 1L;
    private String subjectName = "Mathematics";
    private String subjectDescription = "Mathematics Knowledge Graph";
    private final List<KnowledgeGraphNodeDTO> nodes = new ArrayList<>();
    private final List<KnowledgeGraphEdgeDTO> edges = new ArrayList<>();

    public static KnowledgeGraphDTOBuilder aGraph() {
        return new KnowledgeGraphDTOBuilder();
    }

    public static KnowledgeGraphDTOBuilder forSubject(Long subjectId, String subjectName) {
        KnowledgeGraphDTOBuilder builder = new KnowledgeGraphDTOBuilder();
        builder.subjectId = subjectId;
        builder.subjectName = subjectName;
        builder.subjectDescription = subjectName + " Knowledge Graph";
        return builder;
    }

    public KnowledgeGraphDTOBuilder withSubject(Long subjectId, String subjectName, String subjectDescription) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.subjectDescription = subjectDescription;
        return this;
    }

    public KnowledgeGraphDTOBuilder addNode(Long id, String title, String type) {
        this.nodes.add(new KnowledgeGraphNodeDTO(id, title, type, this.subjectId));
        return this;
    }

    public KnowledgeGraphDTOBuilder addNode(Long id, String title, String type, Long nodeSubjectId) {
        this.nodes.add(new KnowledgeGraphNodeDTO(id, title, type, nodeSubjectId));
        return this;
    }

    public KnowledgeGraphDTOBuilder addEdge(Long id, Long fromNodeId, Long toNodeId, String relationType) {
        this.edges.add(new KnowledgeGraphEdgeDTO(id, fromNodeId, toNodeId, relationType));
        return this;
    }

    public KnowledgeGraphDTOBuilder addPrerequisiteEdge(Long id, Long fromNodeId, Long toNodeId) {
        return addEdge(id, fromNodeId, toNodeId, KnowledgeGraphEdgeDTO.RELATION_TYPE_PREREQUISITE);
    }

    public KnowledgeGraphDTO build() {
        KnowledgeGraphDTO dto = new KnowledgeGraphDTO(this.subjectId, this.subjectName, this.subjectDescription);
        dto.setNodes(new ArrayList<>(this.nodes));
        dto.setEdges(new ArrayList<>(this.edges));
        return dto;
    }
}

