package com.lumi.service;

import com.lumi.service.dto.TicketMessageDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.TicketMessage}.
 */
public interface TicketMessageService {
    /**
     * Save a ticketMessage.
     *
     * @param ticketMessageDTO the entity to save.
     * @return the persisted entity.
     */
    TicketMessageDTO save(TicketMessageDTO ticketMessageDTO);

    /**
     * Updates a ticketMessage.
     *
     * @param ticketMessageDTO the entity to update.
     * @return the persisted entity.
     */
    TicketMessageDTO update(TicketMessageDTO ticketMessageDTO);

    /**
     * Partially updates a ticketMessage.
     *
     * @param ticketMessageDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TicketMessageDTO> partialUpdate(TicketMessageDTO ticketMessageDTO);

    /**
     * Get all the ticketMessages with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TicketMessageDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" ticketMessage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TicketMessageDTO> findOne(Long id);

    /**
     * Delete the "id" ticketMessage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
