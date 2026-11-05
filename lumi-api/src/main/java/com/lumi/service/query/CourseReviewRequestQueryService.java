package com.lumi.service.query;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.CourseReviewRequest;
import com.lumi.repository.CourseReviewRequestRepository;
import com.lumi.service.criteria.CourseReviewRequestCriteria;
import com.lumi.service.dto.CourseReviewRequestDTO;
import com.lumi.service.mapper.CourseReviewRequestMapper;
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
 * Service for executing complex queries for {@link CourseReviewRequest} entities in the database.
 * The main input is a {@link CourseReviewRequestCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CourseReviewRequestDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CourseReviewRequestQueryService extends QueryService<CourseReviewRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(CourseReviewRequestQueryService.class);

    private final CourseReviewRequestRepository courseReviewRequestRepository;

    private final CourseReviewRequestMapper courseReviewRequestMapper;

    public CourseReviewRequestQueryService(
        CourseReviewRequestRepository courseReviewRequestRepository,
        CourseReviewRequestMapper courseReviewRequestMapper
    ) {
        this.courseReviewRequestRepository = courseReviewRequestRepository;
        this.courseReviewRequestMapper = courseReviewRequestMapper;
    }

    /**
     * Return a {@link Page} of {@link CourseReviewRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CourseReviewRequestDTO> findByCriteria(CourseReviewRequestCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CourseReviewRequest> specification = createSpecification(criteria);
        return courseReviewRequestRepository.findAll(specification, page).map(courseReviewRequestMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CourseReviewRequestCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CourseReviewRequest> specification = createSpecification(criteria);
        return courseReviewRequestRepository.count(specification);
    }

    /**
     * Function to convert {@link CourseReviewRequestCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CourseReviewRequest> createSpecification(CourseReviewRequestCriteria criteria) {
        Specification<CourseReviewRequest> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(CourseReviewRequest_.course, JoinType.LEFT);
                root.fetch(CourseReviewRequest_.submittedBy, JoinType.LEFT);
                root.fetch(CourseReviewRequest_.reviewedBy, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), CourseReviewRequest_.id),
                    buildRangeSpecification(criteria.getCourseVersion(), CourseReviewRequest_.courseVersion),
                    buildSpecification(criteria.getStatus(), root -> root.get(CourseReviewRequest_.status).as(String.class)),
                    buildStringSpecification(criteria.getSubmissionNote(), CourseReviewRequest_.submissionNote),
                    buildRangeSpecification(criteria.getRightsConfirmedAt(), CourseReviewRequest_.rightsConfirmedAt),
                    buildRangeSpecification(criteria.getSubmittedAt(), CourseReviewRequest_.submittedAt),
                    buildRangeSpecification(criteria.getReviewedAt(), CourseReviewRequest_.reviewedAt),
                    buildStringSpecification(criteria.getReviewerFeedback(), CourseReviewRequest_.reviewerFeedback),
                    buildSpecification(criteria.getCourseId(), root ->
                        root.join(CourseReviewRequest_.course, JoinType.LEFT).get(Course_.id)
                    ),
                    buildSpecification(criteria.getSubmittedById(), root ->
                        root.join(CourseReviewRequest_.submittedBy, JoinType.LEFT).get(User_.id)
                    ),
                    buildSpecification(criteria.getReviewedById(), root ->
                        root.join(CourseReviewRequest_.reviewedBy, JoinType.LEFT).get(User_.id)
                    )
                )
            );
        }
        return specification;
    }
}
