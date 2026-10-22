package com.lumi.service;

import com.lumi.service.dto.StudentStreakDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.domain.StudentStreak}.
 */
public interface StudentStreakService {
    /**
     * Save a studentStreak.
     *
     * @param studentStreakDTO the entity to save.
     * @return the persisted entity.
     */
    StudentStreakDTO save(StudentStreakDTO studentStreakDTO);

    /**
     * Updates a studentStreak.
     *
     * @param studentStreakDTO the entity to update.
     * @return the persisted entity.
     */
    StudentStreakDTO update(StudentStreakDTO studentStreakDTO);

    /**
     * Partially updates a studentStreak.
     *
     * @param studentStreakDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<StudentStreakDTO> partialUpdate(StudentStreakDTO studentStreakDTO);

    /**
     * Get all the studentStreaks with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    List<StudentStreakDTO> findAllWithEagerRelationships();

    /**
     * Get the "id" studentStreak.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<StudentStreakDTO> findOne(Long id);

    /**
     * Delete the "id" studentStreak.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
