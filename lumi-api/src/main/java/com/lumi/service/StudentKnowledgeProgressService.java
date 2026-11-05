package com.lumi.service;

import com.lumi.service.dto.StudentKnowledgeProgressDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.StudentKnowledgeProgress}.
 */
public interface StudentKnowledgeProgressService {
    /**
     * Save a studentKnowledgeProgress.
     *
     * @param studentKnowledgeProgressDTO the entity to save.
     * @return the persisted entity.
     */
    StudentKnowledgeProgressDTO save(StudentKnowledgeProgressDTO studentKnowledgeProgressDTO);

    /**
     * Updates a studentKnowledgeProgress.
     *
     * @param studentKnowledgeProgressDTO the entity to update.
     * @return the persisted entity.
     */
    StudentKnowledgeProgressDTO update(StudentKnowledgeProgressDTO studentKnowledgeProgressDTO);

    /**
     * Partially updates a studentKnowledgeProgress.
     *
     * @param studentKnowledgeProgressDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<StudentKnowledgeProgressDTO> partialUpdate(StudentKnowledgeProgressDTO studentKnowledgeProgressDTO);

    /**
     * Get all the studentKnowledgeProgresses with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StudentKnowledgeProgressDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" studentKnowledgeProgress.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<StudentKnowledgeProgressDTO> findOne(Long id);

    /**
     * Delete the "id" studentKnowledgeProgress.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
