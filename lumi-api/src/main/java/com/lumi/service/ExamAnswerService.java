package com.lumi.service;

import com.lumi.service.dto.ExamAnswerDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.domain.ExamAnswer}.
 */
public interface ExamAnswerService {
    /**
     * Save a examAnswer.
     *
     * @param examAnswerDTO the entity to save.
     * @return the persisted entity.
     */
    ExamAnswerDTO save(ExamAnswerDTO examAnswerDTO);

    /**
     * Updates a examAnswer.
     *
     * @param examAnswerDTO the entity to update.
     * @return the persisted entity.
     */
    ExamAnswerDTO update(ExamAnswerDTO examAnswerDTO);

    /**
     * Partially updates a examAnswer.
     *
     * @param examAnswerDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ExamAnswerDTO> partialUpdate(ExamAnswerDTO examAnswerDTO);

    /**
     * Get the "id" examAnswer.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ExamAnswerDTO> findOne(Long id);

    /**
     * Delete the "id" examAnswer.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
