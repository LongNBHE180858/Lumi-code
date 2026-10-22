package com.lumi.service;

import com.lumi.service.dto.ContentReportDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.ContentReport}.
 */
public interface ContentReportService {
    /**
     * Save a contentReport.
     *
     * @param contentReportDTO the entity to save.
     * @return the persisted entity.
     */
    ContentReportDTO save(ContentReportDTO contentReportDTO);

    /**
     * Updates a contentReport.
     *
     * @param contentReportDTO the entity to update.
     * @return the persisted entity.
     */
    ContentReportDTO update(ContentReportDTO contentReportDTO);

    /**
     * Partially updates a contentReport.
     *
     * @param contentReportDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ContentReportDTO> partialUpdate(ContentReportDTO contentReportDTO);

    /**
     * Get all the contentReports with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ContentReportDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" contentReport.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ContentReportDTO> findOne(Long id);

    /**
     * Delete the "id" contentReport.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
