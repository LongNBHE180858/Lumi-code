package com.lumi.service;

import com.lumi.service.dto.ClassMemberDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.ClassMember}.
 */
public interface ClassMemberService {
    /**
     * Save a classMember.
     *
     * @param classMemberDTO the entity to save.
     * @return the persisted entity.
     */
    ClassMemberDTO save(ClassMemberDTO classMemberDTO);

    /**
     * Updates a classMember.
     *
     * @param classMemberDTO the entity to update.
     * @return the persisted entity.
     */
    ClassMemberDTO update(ClassMemberDTO classMemberDTO);

    /**
     * Partially updates a classMember.
     *
     * @param classMemberDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ClassMemberDTO> partialUpdate(ClassMemberDTO classMemberDTO);

    /**
     * Get all the classMembers with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ClassMemberDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" classMember.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ClassMemberDTO> findOne(Long id);

    /**
     * Delete the "id" classMember.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
