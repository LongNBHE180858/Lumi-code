package com.lumi.service;

import com.lumi.service.dto.DiscussionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.Discussion}.
 */
public interface DiscussionService {
    /**
     * Save a discussion.
     *
     * @param discussionDTO the entity to save.
     * @return the persisted entity.
     */
    DiscussionDTO save(DiscussionDTO discussionDTO);

    /**
     * Updates a discussion.
     *
     * @param discussionDTO the entity to update.
     * @return the persisted entity.
     */
    DiscussionDTO update(DiscussionDTO discussionDTO);

    /**
     * Partially updates a discussion.
     *
     * @param discussionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DiscussionDTO> partialUpdate(DiscussionDTO discussionDTO);

    /**
     * Get all the discussions with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DiscussionDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" discussion.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DiscussionDTO> findOne(Long id);

    /**
     * Delete the "id" discussion.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
