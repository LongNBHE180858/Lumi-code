package com.lumi.service.query;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.CourseEnrollment;
import com.lumi.repository.CourseEnrollmentRepository;
import com.lumi.service.criteria.CourseEnrollmentCriteria;
import com.lumi.service.dto.CourseEnrollmentDTO;
import com.lumi.service.mapper.CourseEnrollmentMapper;
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
 * Service for executing complex queries for {@link CourseEnrollment} entities in the database.
 * The main input is a {@link CourseEnrollmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CourseEnrollmentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CourseEnrollmentQueryService extends QueryService<CourseEnrollment> {

    private static final Logger LOG = LoggerFactory.getLogger(CourseEnrollmentQueryService.class);

    private final CourseEnrollmentRepository courseEnrollmentRepository;

    private final CourseEnrollmentMapper courseEnrollmentMapper;

    public CourseEnrollmentQueryService(
        CourseEnrollmentRepository courseEnrollmentRepository,
        CourseEnrollmentMapper courseEnrollmentMapper
    ) {
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.courseEnrollmentMapper = courseEnrollmentMapper;
    }

    /**
     * Return a {@link Page} of {@link CourseEnrollmentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CourseEnrollmentDTO> findByCriteria(CourseEnrollmentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CourseEnrollment> specification = createSpecification(criteria);
        return courseEnrollmentRepository.findAll(specification, page).map(courseEnrollmentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CourseEnrollmentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CourseEnrollment> specification = createSpecification(criteria);
        return courseEnrollmentRepository.count(specification);
    }

    /**
     * Function to convert {@link CourseEnrollmentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CourseEnrollment> createSpecification(CourseEnrollmentCriteria criteria) {
        Specification<CourseEnrollment> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(CourseEnrollment_.course, JoinType.LEFT);
                root.fetch(CourseEnrollment_.student, JoinType.LEFT);
                root.fetch(CourseEnrollment_.lastAccessedLesson, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), CourseEnrollment_.id),
                    buildRangeSpecification(criteria.getProgressPercent(), CourseEnrollment_.progressPercent),
                    buildStringSpecification(criteria.getStatus(), CourseEnrollment_.status),
                    buildRangeSpecification(criteria.getEnrolledAt(), CourseEnrollment_.enrolledAt),
                    buildRangeSpecification(criteria.getExpiresAt(), CourseEnrollment_.expiresAt),
                    buildRangeSpecification(criteria.getUpdatedAt(), CourseEnrollment_.updatedAt),
                    buildSpecification(criteria.getCourseId(), root -> root.join(CourseEnrollment_.course, JoinType.LEFT).get(Course_.id)),
                    buildSpecification(criteria.getStudentId(), root -> root.join(CourseEnrollment_.student, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getLastAccessedLessonId(), root ->
                        root.join(CourseEnrollment_.lastAccessedLesson, JoinType.LEFT).get(Lesson_.id)
                    )
                )
            );
        }
        return specification;
    }
}
