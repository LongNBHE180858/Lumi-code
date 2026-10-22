package com.lumi.service;

import com.lumi.service.dto.ExamQuestionDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumi.domain.ExamQuestion}.
 */
public interface ExamQuestionService {
    /**
     * Save a examQuestion.
     *
     * @param examQuestionDTO the entity to save.
     * @return the persisted entity.
     */
    ExamQuestionDTO save(ExamQuestionDTO examQuestionDTO);

    /**
     * Updates a examQuestion.
     *
     * @param examQuestionDTO the entity to update.
     * @return the persisted entity.
     */
    ExamQuestionDTO update(ExamQuestionDTO examQuestionDTO);

    /**
     * Partially updates a examQuestion.
     *
     * @param examQuestionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ExamQuestionDTO> partialUpdate(ExamQuestionDTO examQuestionDTO);

    /**
     * Get all the examQuestions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    List<ExamQuestionDTO> findAllWithEagerRelationships();

    /**
     * Get the "id" examQuestion.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ExamQuestionDTO> findOne(Long id);

    /**
     * Delete the "id" examQuestion.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
