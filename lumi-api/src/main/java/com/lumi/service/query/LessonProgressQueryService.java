package com.lumi.service.query;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.LessonProgress;
import com.lumi.repository.LessonProgressRepository;
import com.lumi.service.criteria.LessonProgressCriteria;
import com.lumi.service.dto.LessonProgressDTO;
import com.lumi.service.mapper.LessonProgressMapper;
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
 * Service for executing complex queries for {@link LessonProgress} entities in the database.
 * The main input is a {@link LessonProgressCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link LessonProgressDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LessonProgressQueryService extends QueryService<LessonProgress> {

    private static final Logger LOG = LoggerFactory.getLogger(LessonProgressQueryService.class);

    private final LessonProgressRepository lessonProgressRepository;

    private final LessonProgressMapper lessonProgressMapper;

    public LessonProgressQueryService(LessonProgressRepository lessonProgressRepository, LessonProgressMapper lessonProgressMapper) {
        this.lessonProgressRepository = lessonProgressRepository;
        this.lessonProgressMapper = lessonProgressMapper;
    }

    /**
     * Return a {@link Page} of {@link LessonProgressDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LessonProgressDTO> findByCriteria(LessonProgressCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<LessonProgress> specification = createSpecification(criteria);
        return lessonProgressRepository.findAll(specification, page).map(lessonProgressMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LessonProgressCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<LessonProgress> specification = createSpecification(criteria);
        return lessonProgressRepository.count(specification);
    }

    /**
     * Function to convert {@link LessonProgressCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<LessonProgress> createSpecification(LessonProgressCriteria criteria) {
        Specification<LessonProgress> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(LessonProgress_.enrollment, JoinType.LEFT);
                root.fetch(LessonProgress_.lesson, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), LessonProgress_.id),
                    buildStringSpecification(criteria.getStatus(), LessonProgress_.status),
                    buildRangeSpecification(criteria.getLastPositionSeconds(), LessonProgress_.lastPositionSeconds),
                    buildRangeSpecification(criteria.getLastAccessedAt(), LessonProgress_.lastAccessedAt),
                    buildRangeSpecification(criteria.getCompletedAt(), LessonProgress_.completedAt),
                    buildRangeSpecification(criteria.getRowVersion(), LessonProgress_.rowVersion),
                    buildSpecification(criteria.getEnrollmentId(), root ->
                        root.join(LessonProgress_.enrollment, JoinType.LEFT).get(CourseEnrollment_.id)
                    ),
                    buildSpecification(criteria.getLessonId(), root -> root.join(LessonProgress_.lesson, JoinType.LEFT).get(Lesson_.id))
                )
            );
        }
        return specification;
    }
}
