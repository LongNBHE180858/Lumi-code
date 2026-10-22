package com.lumi.service;

import com.lumi.service.dto.AiMessageDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.domain.AiMessage}.
 */
public interface AiMessageService {
    /**
     * Save a aiMessage.
     *
     * @param aiMessageDTO the entity to save.
     * @return the persisted entity.
     */
    AiMessageDTO save(AiMessageDTO aiMessageDTO);

    /**
     * Updates a aiMessage.
     *
     * @param aiMessageDTO the entity to update.
     * @return the persisted entity.
     */
    AiMessageDTO update(AiMessageDTO aiMessageDTO);

    /**
     * Partially updates a aiMessage.
     *
     * @param aiMessageDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AiMessageDTO> partialUpdate(AiMessageDTO aiMessageDTO);

    /**
     * Get the "id" aiMessage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AiMessageDTO> findOne(Long id);

    /**
     * Delete the "id" aiMessage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
