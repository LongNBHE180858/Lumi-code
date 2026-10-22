package com.lumi.service;

import com.lumi.service.dto.SupportTicketDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.SupportTicket}.
 */
public interface SupportTicketService {
    /**
     * Save a supportTicket.
     *
     * @param supportTicketDTO the entity to save.
     * @return the persisted entity.
     */
    SupportTicketDTO save(SupportTicketDTO supportTicketDTO);

    /**
     * Updates a supportTicket.
     *
     * @param supportTicketDTO the entity to update.
     * @return the persisted entity.
     */
    SupportTicketDTO update(SupportTicketDTO supportTicketDTO);

    /**
     * Partially updates a supportTicket.
     *
     * @param supportTicketDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SupportTicketDTO> partialUpdate(SupportTicketDTO supportTicketDTO);

    /**
     * Get all the supportTickets with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SupportTicketDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" supportTicket.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SupportTicketDTO> findOne(Long id);

    /**
     * Delete the "id" supportTicket.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
