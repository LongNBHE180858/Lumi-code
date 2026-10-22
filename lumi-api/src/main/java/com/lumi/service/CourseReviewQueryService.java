package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.CourseReview;
import com.lumi.repository.CourseReviewRepository;
import com.lumi.service.criteria.CourseReviewCriteria;
import com.lumi.service.dto.CourseReviewDTO;
import com.lumi.service.mapper.CourseReviewMapper;
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
 * Service for executing complex queries for {@link CourseReview} entities in the database.
 * The main input is a {@link CourseReviewCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CourseReviewDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CourseReviewQueryService extends QueryService<CourseReview> {

    private static final Logger LOG = LoggerFactory.getLogger(CourseReviewQueryService.class);

    private final CourseReviewRepository courseReviewRepository;

    private final CourseReviewMapper courseReviewMapper;

    public CourseReviewQueryService(CourseReviewRepository courseReviewRepository, CourseReviewMapper courseReviewMapper) {
        this.courseReviewRepository = courseReviewRepository;
        this.courseReviewMapper = courseReviewMapper;
    }

    /**
     * Return a {@link Page} of {@link CourseReviewDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CourseReviewDTO> findByCriteria(CourseReviewCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CourseReview> specification = createSpecification(criteria);
        return courseReviewRepository.findAll(specification, page).map(courseReviewMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CourseReviewCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CourseReview> specification = createSpecification(criteria);
        return courseReviewRepository.count(specification);
    }

    /**
     * Function to convert {@link CourseReviewCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CourseReview> createSpecification(CourseReviewCriteria criteria) {
        Specification<CourseReview> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(CourseReview_.student, JoinType.LEFT);
                root.fetch(CourseReview_.course, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), CourseReview_.id),
                    buildRangeSpecification(criteria.getRating(), CourseReview_.rating),
                    buildStringSpecification(criteria.getContent(), CourseReview_.content),
                    buildRangeSpecification(criteria.getCreatedAt(), CourseReview_.createdAt),
                    buildSpecification(criteria.getStudentId(), root -> root.join(CourseReview_.student, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getCourseId(), root -> root.join(CourseReview_.course, JoinType.LEFT).get(Course_.id))
                )
            );
        }
        return specification;
    }
}
