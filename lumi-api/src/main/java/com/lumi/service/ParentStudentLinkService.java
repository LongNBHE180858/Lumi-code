package com.lumi.service;

import com.lumi.service.dto.ParentStudentLinkDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.ParentStudentLink}.
 */
public interface ParentStudentLinkService {
    /**
     * Save a parentStudentLink.
     *
     * @param parentStudentLinkDTO the entity to save.
     * @return the persisted entity.
     */
    ParentStudentLinkDTO save(ParentStudentLinkDTO parentStudentLinkDTO);

    /**
     * Updates a parentStudentLink.
     *
     * @param parentStudentLinkDTO the entity to update.
     * @return the persisted entity.
     */
    ParentStudentLinkDTO update(ParentStudentLinkDTO parentStudentLinkDTO);

    /**
     * Partially updates a parentStudentLink.
     *
     * @param parentStudentLinkDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ParentStudentLinkDTO> partialUpdate(ParentStudentLinkDTO parentStudentLinkDTO);

    /**
     * Get all the parentStudentLinks with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ParentStudentLinkDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" parentStudentLink.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ParentStudentLinkDTO> findOne(Long id);

    /**
     * Delete the "id" parentStudentLink.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
