package com.lumi.service.query;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.Wishlist;
import com.lumi.repository.WishlistRepository;
import com.lumi.service.criteria.WishlistCriteria;
import com.lumi.service.dto.WishlistDTO;
import com.lumi.service.mapper.WishlistMapper;
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
 * Service for executing complex queries for {@link Wishlist} entities in the database.
 * The main input is a {@link WishlistCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link WishlistDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WishlistQueryService extends QueryService<Wishlist> {

    private static final Logger LOG = LoggerFactory.getLogger(WishlistQueryService.class);

    private final WishlistRepository wishlistRepository;

    private final WishlistMapper wishlistMapper;

    public WishlistQueryService(WishlistRepository wishlistRepository, WishlistMapper wishlistMapper) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistMapper = wishlistMapper;
    }

    /**
     * Return a {@link Page} of {@link WishlistDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WishlistDTO> findByCriteria(WishlistCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Wishlist> specification = createSpecification(criteria);
        return wishlistRepository.findAll(specification, page).map(wishlistMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WishlistCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Wishlist> specification = createSpecification(criteria);
        return wishlistRepository.count(specification);
    }

    /**
     * Function to convert {@link WishlistCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Wishlist> createSpecification(WishlistCriteria criteria) {
        Specification<Wishlist> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Wishlist_.student, JoinType.LEFT);
                root.fetch(Wishlist_.course, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Wishlist_.id),
                    buildRangeSpecification(criteria.getCreatedAt(), Wishlist_.createdAt),
                    buildSpecification(criteria.getStudentId(), root -> root.join(Wishlist_.student, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getCourseId(), root -> root.join(Wishlist_.course, JoinType.LEFT).get(Course_.id))
                )
            );
        }
        return specification;
    }
}
