package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.AssignmentSubmission;
import com.lumi.repository.AssignmentSubmissionRepository;
import com.lumi.service.criteria.AssignmentSubmissionCriteria;
import com.lumi.service.dto.AssignmentSubmissionDTO;
import com.lumi.service.mapper.AssignmentSubmissionMapper;
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
 * Service for executing complex queries for {@link AssignmentSubmission} entities in the database.
 * The main input is a {@link AssignmentSubmissionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AssignmentSubmissionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AssignmentSubmissionQueryService extends QueryService<AssignmentSubmission> {

    private static final Logger LOG = LoggerFactory.getLogger(AssignmentSubmissionQueryService.class);

    private final AssignmentSubmissionRepository assignmentSubmissionRepository;

    private final AssignmentSubmissionMapper assignmentSubmissionMapper;

    public AssignmentSubmissionQueryService(
        AssignmentSubmissionRepository assignmentSubmissionRepository,
        AssignmentSubmissionMapper assignmentSubmissionMapper
    ) {
        this.assignmentSubmissionRepository = assignmentSubmissionRepository;
        this.assignmentSubmissionMapper = assignmentSubmissionMapper;
    }

    /**
     * Return a {@link Page} of {@link AssignmentSubmissionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AssignmentSubmissionDTO> findByCriteria(AssignmentSubmissionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AssignmentSubmission> specification = createSpecification(criteria);
        return assignmentSubmissionRepository.findAll(specification, page).map(assignmentSubmissionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AssignmentSubmissionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<AssignmentSubmission> specification = createSpecification(criteria);
        return assignmentSubmissionRepository.count(specification);
    }

    /**
     * Function to convert {@link AssignmentSubmissionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AssignmentSubmission> createSpecification(AssignmentSubmissionCriteria criteria) {
        Specification<AssignmentSubmission> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(AssignmentSubmission_.student, JoinType.LEFT);
                root.fetch(AssignmentSubmission_.assignment, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), AssignmentSubmission_.id),
                    buildStringSpecification(criteria.getContent(), AssignmentSubmission_.content),
                    buildRangeSpecification(criteria.getScore(), AssignmentSubmission_.score),
                    buildStringSpecification(criteria.getTeacherFeedback(), AssignmentSubmission_.teacherFeedback),
                    buildStringSpecification(criteria.getStatus(), AssignmentSubmission_.status),
                    buildRangeSpecification(criteria.getSubmittedAt(), AssignmentSubmission_.submittedAt),
                    buildSpecification(criteria.getStudentId(), root ->
                        root.join(AssignmentSubmission_.student, JoinType.LEFT).get(User_.id)
                    ),
                    buildSpecification(criteria.getAssignmentId(), root ->
                        root.join(AssignmentSubmission_.assignment, JoinType.LEFT).get(Assignment_.id)
                    )
                )
            );
        }
        return specification;
    }
}
