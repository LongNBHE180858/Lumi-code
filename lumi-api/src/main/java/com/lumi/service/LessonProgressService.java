package com.lumi.service;

import com.lumi.service.dto.LessonProgressDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.domain.LessonProgress}.
 */
public interface LessonProgressService {
    /**
     * Save a lessonProgress.
     *
     * @param lessonProgressDTO the entity to save.
     * @return the persisted entity.
     */
    LessonProgressDTO save(LessonProgressDTO lessonProgressDTO);

    /**
     * Updates a lessonProgress.
     *
     * @param lessonProgressDTO the entity to update.
     * @return the persisted entity.
     */
    LessonProgressDTO update(LessonProgressDTO lessonProgressDTO);

    /**
     * Partially updates a lessonProgress.
     *
     * @param lessonProgressDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<LessonProgressDTO> partialUpdate(LessonProgressDTO lessonProgressDTO);

    /**
     * Get the "id" lessonProgress.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LessonProgressDTO> findOne(Long id);

    /**
     * Delete the "id" lessonProgress.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
