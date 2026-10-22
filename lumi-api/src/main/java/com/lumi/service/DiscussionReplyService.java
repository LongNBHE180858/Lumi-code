package com.lumi.service;

import com.lumi.service.dto.DiscussionReplyDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.DiscussionReply}.
 */
public interface DiscussionReplyService {
    /**
     * Save a discussionReply.
     *
     * @param discussionReplyDTO the entity to save.
     * @return the persisted entity.
     */
    DiscussionReplyDTO save(DiscussionReplyDTO discussionReplyDTO);

    /**
     * Updates a discussionReply.
     *
     * @param discussionReplyDTO the entity to update.
     * @return the persisted entity.
     */
    DiscussionReplyDTO update(DiscussionReplyDTO discussionReplyDTO);

    /**
     * Partially updates a discussionReply.
     *
     * @param discussionReplyDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DiscussionReplyDTO> partialUpdate(DiscussionReplyDTO discussionReplyDTO);

    /**
     * Get all the discussionReplies with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DiscussionReplyDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" discussionReply.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DiscussionReplyDTO> findOne(Long id);

    /**
     * Delete the "id" discussionReply.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
