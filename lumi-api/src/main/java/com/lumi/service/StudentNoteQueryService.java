package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.StudentNote;
import com.lumi.repository.StudentNoteRepository;
import com.lumi.service.criteria.StudentNoteCriteria;
import com.lumi.service.dto.StudentNoteDTO;
import com.lumi.service.mapper.StudentNoteMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link StudentNote} entities in the database.
 * The main input is a {@link StudentNoteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link StudentNoteDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StudentNoteQueryService extends QueryService<StudentNote> {

    private static final Logger LOG = LoggerFactory.getLogger(StudentNoteQueryService.class);

    private final StudentNoteRepository studentNoteRepository;

    private final StudentNoteMapper studentNoteMapper;

    public StudentNoteQueryService(StudentNoteRepository studentNoteRepository, StudentNoteMapper studentNoteMapper) {
        this.studentNoteRepository = studentNoteRepository;
        this.studentNoteMapper = studentNoteMapper;
    }

    /**
     * Return a {@link Page} of {@link StudentNoteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StudentNoteDTO> findByCriteria(StudentNoteCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<StudentNote> specification = createSpecification(criteria);
        return studentNoteRepository.findAll(specification, page).map(studentNoteMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StudentNoteCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<StudentNote> specification = createSpecification(criteria);
        return studentNoteRepository.count(specification);
    }

    /**
     * Function to convert {@link StudentNoteCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StudentNote> createSpecification(StudentNoteCriteria criteria) {
        Specification<StudentNote> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(StudentNote_.student, JoinType.LEFT);
                root.fetch(StudentNote_.lesson, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), StudentNote_.id),
                    buildRangeSpecification(criteria.getVideoTimestamp(), StudentNote_.videoTimestamp),
                    buildStringSpecification(criteria.getContent(), StudentNote_.content),
                    buildRangeSpecification(criteria.getCreatedAt(), StudentNote_.createdAt),
                    buildSpecification(criteria.getStudentId(), root -> root.join(StudentNote_.student, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getLessonId(), root -> root.join(StudentNote_.lesson, JoinType.LEFT).get(Lesson_.id))
                )
            );
        }
        return specification;
    }
}
