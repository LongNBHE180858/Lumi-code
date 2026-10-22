package com.lumi.service;

import com.lumi.service.dto.KnowledgeNodeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.KnowledgeNode}.
 */
public interface KnowledgeNodeService {
    /**
     * Save a knowledgeNode.
     *
     * @param knowledgeNodeDTO the entity to save.
     * @return the persisted entity.
     */
    KnowledgeNodeDTO save(KnowledgeNodeDTO knowledgeNodeDTO);

    /**
     * Updates a knowledgeNode.
     *
     * @param knowledgeNodeDTO the entity to update.
     * @return the persisted entity.
     */
    KnowledgeNodeDTO update(KnowledgeNodeDTO knowledgeNodeDTO);

    /**
     * Partially updates a knowledgeNode.
     *
     * @param knowledgeNodeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<KnowledgeNodeDTO> partialUpdate(KnowledgeNodeDTO knowledgeNodeDTO);

    /**
     * Get all the knowledgeNodes with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<KnowledgeNodeDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" knowledgeNode.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<KnowledgeNodeDTO> findOne(Long id);

    /**
     * Delete the "id" knowledgeNode.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
