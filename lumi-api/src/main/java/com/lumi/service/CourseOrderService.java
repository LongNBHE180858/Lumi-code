package com.lumi.service;

import com.lumi.service.dto.CourseOrderDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.CourseOrder}.
 */
public interface CourseOrderService {
    /**
     * Save a courseOrder.
     *
     * @param courseOrderDTO the entity to save.
     * @return the persisted entity.
     */
    CourseOrderDTO save(CourseOrderDTO courseOrderDTO);

    /**
     * Updates a courseOrder.
     *
     * @param courseOrderDTO the entity to update.
     * @return the persisted entity.
     */
    CourseOrderDTO update(CourseOrderDTO courseOrderDTO);

    /**
     * Partially updates a courseOrder.
     *
     * @param courseOrderDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CourseOrderDTO> partialUpdate(CourseOrderDTO courseOrderDTO);

    /**
     * Get all the courseOrders with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CourseOrderDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" courseOrder.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CourseOrderDTO> findOne(Long id);

    /**
     * Delete the "id" courseOrder.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
