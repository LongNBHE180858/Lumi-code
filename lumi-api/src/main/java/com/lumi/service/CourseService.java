package com.lumi.service;

import com.lumi.service.dto.CourseBrowseRequest;
import com.lumi.service.dto.CourseBrowseResponse;
import com.lumi.service.dto.CourseDTO;
import com.lumi.service.dto.CourseOverviewResponse;
import com.lumi.service.dto.CourseReviewsResponse;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.Course}.
 */
public interface CourseService {
    /**
     * Save a course.
     *
     * @param courseDTO the entity to save.
     * @return the persisted entity.
     */
    CourseDTO save(CourseDTO courseDTO);

    /**
     * Updates a course.
     *
     * @param courseDTO the entity to update.
     * @return the persisted entity.
     */
    CourseDTO update(CourseDTO courseDTO);

    /**
     * Partially updates a course.
     *
     * @param courseDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CourseDTO> partialUpdate(CourseDTO courseDTO);

    /**
     * Get all the courses with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CourseDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" course.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CourseDTO> findOne(Long id);

    /**
     * Delete the "id" course.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    CourseBrowseResponse browse(CourseBrowseRequest request, Pageable pageable);

    CourseOverviewResponse overview(Long id);

    CourseReviewsResponse reviews(Long id, Pageable pageable);
}
