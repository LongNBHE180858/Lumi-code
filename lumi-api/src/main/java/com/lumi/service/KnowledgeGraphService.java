package com.lumi.service;

import com.lumi.service.dto.KnowledgeGraphDTO;
import com.lumi.service.dto.SubjectDTO;
import java.util.List;

/**
 * Service Interface for managing the Knowledge Graph of a Subject.
 */
public interface KnowledgeGraphService {

    /**
     * Get list of supported subjects for Knowledge Graph management (Mathematics and History).
     *
     * @return list of SubjectDTOs.
     */
    List<SubjectDTO> getAvailableSubjects();

    /**
     * Load the knowledge graph for a subject.
     *
     * @param subjectId the identifier of the subject.
     * @return the complete KnowledgeGraphDTO.
     */
    KnowledgeGraphDTO loadGraph(Long subjectId);

    /**
     * Validate a candidate knowledge graph without persisting.
     *
     * @param subjectId the subject identifier.
     * @param candidateGraph the complete candidate graph.
     */
    void validateGraph(Long subjectId, KnowledgeGraphDTO candidateGraph);

    /**
     * Atomically validate and persist the complete knowledge graph for a subject.
     *
     * @param subjectId the subject identifier.
     * @param candidateGraph the complete candidate graph to persist.
     * @return the saved KnowledgeGraphDTO with stable persistent IDs.
     */
    KnowledgeGraphDTO saveGraph(Long subjectId, KnowledgeGraphDTO candidateGraph);
}
