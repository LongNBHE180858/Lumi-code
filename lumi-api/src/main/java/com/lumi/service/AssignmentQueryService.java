package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.Assignment;
import com.lumi.repository.AssignmentRepository;
import com.lumi.service.criteria.AssignmentCriteria;
import com.lumi.service.dto.AssignmentDTO;
import com.lumi.service.mapper.AssignmentMapper;
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
 * Service for executing complex queries for {@link Assignment} entities in the database.
 * The main input is a {@link AssignmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AssignmentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AssignmentQueryService extends QueryService<Assignment> {

    private static final Logger LOG = LoggerFactory.getLogger(AssignmentQueryService.class);

    private final AssignmentRepository assignmentRepository;

    private final AssignmentMapper assignmentMapper;

    public AssignmentQueryService(AssignmentRepository assignmentRepository, AssignmentMapper assignmentMapper) {
        this.assignmentRepository = assignmentRepository;
        this.assignmentMapper = assignmentMapper;
    }

    /**
     * Return a {@link Page} of {@link AssignmentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AssignmentDTO> findByCriteria(AssignmentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Assignment> specification = createSpecification(criteria);
        return assignmentRepository.findAll(specification, page).map(assignmentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AssignmentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Assignment> specification = createSpecification(criteria);
        return assignmentRepository.count(specification);
    }

    /**
     * Function to convert {@link AssignmentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Assignment> createSpecification(AssignmentCriteria criteria) {
        Specification<Assignment> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Assignment_.studyClass, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Assignment_.id),
                    buildStringSpecification(criteria.getTitle(), Assignment_.title),
                    buildStringSpecification(criteria.getInstructions(), Assignment_.instructions),
                    buildRangeSpecification(criteria.getDueAt(), Assignment_.dueAt),
                    buildRangeSpecification(criteria.getMaxScore(), Assignment_.maxScore),
                    buildSpecification(criteria.getAllowLate(), Assignment_.allowLate),
                    buildSpecification(criteria.getIsDeleted(), Assignment_.isDeleted),
                    buildSpecification(criteria.getStudyClassId(), root ->
                        root.join(Assignment_.studyClass, JoinType.LEFT).get(StudyClass_.id)
                    )
                )
            );
        }
        return specification;
    }
}
