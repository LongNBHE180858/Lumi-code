package com.lumi.service;

import com.lumi.service.dto.AiConversationDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.AiConversation}.
 */
public interface AiConversationService {
    /**
     * Save a aiConversation.
     *
     * @param aiConversationDTO the entity to save.
     * @return the persisted entity.
     */
    AiConversationDTO save(AiConversationDTO aiConversationDTO);

    /**
     * Updates a aiConversation.
     *
     * @param aiConversationDTO the entity to update.
     * @return the persisted entity.
     */
    AiConversationDTO update(AiConversationDTO aiConversationDTO);

    /**
     * Partially updates a aiConversation.
     *
     * @param aiConversationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AiConversationDTO> partialUpdate(AiConversationDTO aiConversationDTO);

    /**
     * Get all the aiConversations with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AiConversationDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" aiConversation.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AiConversationDTO> findOne(Long id);

    /**
     * Delete the "id" aiConversation.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
