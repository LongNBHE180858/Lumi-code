package com.lumi.service.query;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.Course;
import com.lumi.repository.CourseRepository;
import com.lumi.service.criteria.CourseCriteria;
import com.lumi.service.dto.CourseDTO;
import com.lumi.service.mapper.CourseMapper;
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
 * Service for executing complex queries for {@link Course} entities in the database.
 * The main input is a {@link CourseCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CourseDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CourseQueryService extends QueryService<Course> {

    private static final Logger LOG = LoggerFactory.getLogger(CourseQueryService.class);

    private final CourseRepository courseRepository;

    private final CourseMapper courseMapper;

    public CourseQueryService(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    /**
     * Return a {@link Page} of {@link CourseDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CourseDTO> findByCriteria(CourseCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Course> specification = createSpecification(criteria);
        return courseRepository.findAll(specification, page).map(courseMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<CourseDTO> findForInstructor(CourseCriteria criteria, Pageable page, Long authorId) {
        CourseCriteria scoped = criteria == null ? new CourseCriteria() : criteria.copy();
        // Ownership and deletion visibility are always controlled by the server.
        scoped.setAuthorId(null);
        scoped.setIsDeleted(null);
        Specification<Course> specification = createSpecification(scoped).and((root, query, cb) ->
            cb.and(
                cb.equal(root.get(Course_.author).get(User_.id), authorId),
                cb.or(cb.isFalse(root.get(Course_.isDeleted)), cb.isNull(root.get(Course_.isDeleted)))
            )
        );
        return courseRepository.findAll(specification, page).map(courseMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CourseCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Course> specification = createSpecification(criteria);
        return courseRepository.count(specification);
    }

    /**
     * Function to convert {@link CourseCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Course> createSpecification(CourseCriteria criteria) {
        Specification<Course> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Course_.author, JoinType.LEFT);
                root.fetch(Course_.subject, JoinType.LEFT);
                root.fetch(Course_.grade, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Course_.id),
                    buildStringSpecification(criteria.getTitle(), Course_.title),
                    buildStringSpecification(criteria.getThumbnailUrl(), Course_.thumbnailUrl),
                    buildRangeSpecification(criteria.getPrice(), Course_.price),
                    buildStringSpecification(criteria.getShortDescription(), Course_.shortDescription),
                    buildStringSpecification(criteria.getBookSeries(), Course_.bookSeries),
                    buildStringSpecification(criteria.getTargetAudience(), Course_.targetAudience),
                    buildStringSpecification(criteria.getPrerequisites(), Course_.prerequisites),
                    buildStringSpecification(criteria.getIntroVideoUrl(), Course_.introVideoUrl),
                    buildRangeSpecification(criteria.getValidDurationDays(), Course_.validDurationDays),
                    buildRangeSpecification(criteria.getAccessDurationValue(), Course_.accessDurationValue),
                    buildSpecification(criteria.getAccessDurationUnit(), root -> root.get(Course_.accessDurationUnit).as(String.class)),
                    buildRangeSpecification(criteria.getVersion(), Course_.version),
                    buildSpecification(criteria.getStatus(), Course_.status),
                    buildSpecification(criteria.getIsDeleted(), Course_.isDeleted),
                    buildRangeSpecification(criteria.getCreatedAt(), Course_.createdAt),
                    buildRangeSpecification(criteria.getUpdatedAt(), Course_.updatedAt),
                    buildRangeSpecification(criteria.getStorageLimitBytes(), Course_.storageLimitBytes),
                    buildSpecification(criteria.getAuthorId(), root -> root.join(Course_.author, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getSubjectId(), root -> root.join(Course_.subject, JoinType.LEFT).get(Subject_.id)),
                    buildSpecification(criteria.getGradeId(), root -> root.join(Course_.grade, JoinType.LEFT).get(Grade_.id))
                )
            );
        }
        return specification;
    }
}
