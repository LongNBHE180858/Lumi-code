package com.lumi.service;

import com.lumi.service.dto.AssignmentStudentDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.AssignmentStudent}.
 */
public interface AssignmentStudentService {
    /**
     * Save a assignmentStudent.
     *
     * @param assignmentStudentDTO the entity to save.
     * @return the persisted entity.
     */
    AssignmentStudentDTO save(AssignmentStudentDTO assignmentStudentDTO);

    /**
     * Updates a assignmentStudent.
     *
     * @param assignmentStudentDTO the entity to update.
     * @return the persisted entity.
     */
    AssignmentStudentDTO update(AssignmentStudentDTO assignmentStudentDTO);

    /**
     * Partially updates a assignmentStudent.
     *
     * @param assignmentStudentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AssignmentStudentDTO> partialUpdate(AssignmentStudentDTO assignmentStudentDTO);

    /**
     * Get all the assignmentStudents with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AssignmentStudentDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" assignmentStudent.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AssignmentStudentDTO> findOne(Long id);

    /**
     * Delete the "id" assignmentStudent.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
