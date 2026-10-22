package com.lumi.service;

import com.lumi.service.dto.TeacherVerificationDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.TeacherVerification}.
 */
public interface TeacherVerificationService {
    /**
     * Save a teacherVerification.
     *
     * @param teacherVerificationDTO the entity to save.
     * @return the persisted entity.
     */
    TeacherVerificationDTO save(TeacherVerificationDTO teacherVerificationDTO);

    /**
     * Updates a teacherVerification.
     *
     * @param teacherVerificationDTO the entity to update.
     * @return the persisted entity.
     */
    TeacherVerificationDTO update(TeacherVerificationDTO teacherVerificationDTO);

    /**
     * Partially updates a teacherVerification.
     *
     * @param teacherVerificationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TeacherVerificationDTO> partialUpdate(TeacherVerificationDTO teacherVerificationDTO);

    /**
     * Get all the teacherVerifications with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TeacherVerificationDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" teacherVerification.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TeacherVerificationDTO> findOne(Long id);

    /**
     * Delete the "id" teacherVerification.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
