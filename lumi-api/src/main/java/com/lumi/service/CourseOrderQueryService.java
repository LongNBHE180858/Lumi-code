package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.CourseOrder;
import com.lumi.repository.CourseOrderRepository;
import com.lumi.service.criteria.CourseOrderCriteria;
import com.lumi.service.dto.CourseOrderDTO;
import com.lumi.service.mapper.CourseOrderMapper;
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
 * Service for executing complex queries for {@link CourseOrder} entities in the database.
 * The main input is a {@link CourseOrderCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CourseOrderDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CourseOrderQueryService extends QueryService<CourseOrder> {

    private static final Logger LOG = LoggerFactory.getLogger(CourseOrderQueryService.class);

    private final CourseOrderRepository courseOrderRepository;

    private final CourseOrderMapper courseOrderMapper;

    public CourseOrderQueryService(CourseOrderRepository courseOrderRepository, CourseOrderMapper courseOrderMapper) {
        this.courseOrderRepository = courseOrderRepository;
        this.courseOrderMapper = courseOrderMapper;
    }

    /**
     * Return a {@link Page} of {@link CourseOrderDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CourseOrderDTO> findByCriteria(CourseOrderCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CourseOrder> specification = createSpecification(criteria);
        return courseOrderRepository.findAll(specification, page).map(courseOrderMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CourseOrderCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CourseOrder> specification = createSpecification(criteria);
        return courseOrderRepository.count(specification);
    }

    /**
     * Function to convert {@link CourseOrderCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CourseOrder> createSpecification(CourseOrderCriteria criteria) {
        Specification<CourseOrder> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(CourseOrder_.student, JoinType.LEFT);
                root.fetch(CourseOrder_.course, JoinType.LEFT);
                root.fetch(CourseOrder_.plan, JoinType.LEFT);
                root.fetch(CourseOrder_.coupon, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), CourseOrder_.id),
                    buildRangeSpecification(criteria.getOriginalPrice(), CourseOrder_.originalPrice),
                    buildRangeSpecification(criteria.getDiscountAmount(), CourseOrder_.discountAmount),
                    buildRangeSpecification(criteria.getFinalPrice(), CourseOrder_.finalPrice),
                    buildStringSpecification(criteria.getRevenueSplitRate(), CourseOrder_.revenueSplitRate),
                    buildSpecification(criteria.getStatus(), CourseOrder_.status),
                    buildStringSpecification(criteria.getIdempotencyKey(), CourseOrder_.idempotencyKey),
                    buildRangeSpecification(criteria.getRowVersion(), CourseOrder_.rowVersion),
                    buildRangeSpecification(criteria.getCreatedAt(), CourseOrder_.createdAt),
                    buildSpecification(criteria.getStudentId(), root -> root.join(CourseOrder_.student, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getCourseId(), root -> root.join(CourseOrder_.course, JoinType.LEFT).get(Course_.id)),
                    buildSpecification(criteria.getPlanId(), root -> root.join(CourseOrder_.plan, JoinType.LEFT).get(SystemPlan_.id)),
                    buildSpecification(criteria.getCouponId(), root -> root.join(CourseOrder_.coupon, JoinType.LEFT).get(Coupon_.id))
                )
            );
        }
        return specification;
    }
}
