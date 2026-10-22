package com.lumi.service;

import com.lumi.service.dto.StudentNoteDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.StudentNote}.
 */
public interface StudentNoteService {
    /**
     * Save a studentNote.
     *
     * @param studentNoteDTO the entity to save.
     * @return the persisted entity.
     */
    StudentNoteDTO save(StudentNoteDTO studentNoteDTO);

    /**
     * Updates a studentNote.
     *
     * @param studentNoteDTO the entity to update.
     * @return the persisted entity.
     */
    StudentNoteDTO update(StudentNoteDTO studentNoteDTO);

    /**
     * Partially updates a studentNote.
     *
     * @param studentNoteDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<StudentNoteDTO> partialUpdate(StudentNoteDTO studentNoteDTO);

    /**
     * Get all the studentNotes with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StudentNoteDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" studentNote.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<StudentNoteDTO> findOne(Long id);

    /**
     * Delete the "id" studentNote.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
