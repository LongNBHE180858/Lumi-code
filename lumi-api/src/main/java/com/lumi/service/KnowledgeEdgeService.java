package com.lumi.service;

import com.lumi.service.dto.KnowledgeEdgeDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.domain.KnowledgeEdge}.
 */
public interface KnowledgeEdgeService {
    /**
     * Save a knowledgeEdge.
     *
     * @param knowledgeEdgeDTO the entity to save.
     * @return the persisted entity.
     */
    KnowledgeEdgeDTO save(KnowledgeEdgeDTO knowledgeEdgeDTO);

    /**
     * Updates a knowledgeEdge.
     *
     * @param knowledgeEdgeDTO the entity to update.
     * @return the persisted entity.
     */
    KnowledgeEdgeDTO update(KnowledgeEdgeDTO knowledgeEdgeDTO);

    /**
     * Partially updates a knowledgeEdge.
     *
     * @param knowledgeEdgeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<KnowledgeEdgeDTO> partialUpdate(KnowledgeEdgeDTO knowledgeEdgeDTO);

    /**
     * Get all the knowledgeEdges with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    List<KnowledgeEdgeDTO> findAllWithEagerRelationships();

    /**
     * Get the "id" knowledgeEdge.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<KnowledgeEdgeDTO> findOne(Long id);

    /**
     * Delete the "id" knowledgeEdge.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
