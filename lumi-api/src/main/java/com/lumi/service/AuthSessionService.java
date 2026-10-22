package com.lumi.service;

import com.lumi.service.dto.AuthSessionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.AuthSession}.
 */
public interface AuthSessionService {
    /**
     * Save a authSession.
     *
     * @param authSessionDTO the entity to save.
     * @return the persisted entity.
     */
    AuthSessionDTO save(AuthSessionDTO authSessionDTO);

    /**
     * Updates a authSession.
     *
     * @param authSessionDTO the entity to update.
     * @return the persisted entity.
     */
    AuthSessionDTO update(AuthSessionDTO authSessionDTO);

    /**
     * Partially updates a authSession.
     *
     * @param authSessionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AuthSessionDTO> partialUpdate(AuthSessionDTO authSessionDTO);

    /**
     * Get all the authSessions with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AuthSessionDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" authSession.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AuthSessionDTO> findOne(Long id);

    /**
     * Delete the "id" authSession.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
