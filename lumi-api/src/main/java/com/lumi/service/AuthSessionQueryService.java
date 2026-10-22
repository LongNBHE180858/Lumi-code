package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.AuthSession;
import com.lumi.repository.AuthSessionRepository;
import com.lumi.service.criteria.AuthSessionCriteria;
import com.lumi.service.dto.AuthSessionDTO;
import com.lumi.service.mapper.AuthSessionMapper;
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
 * Service for executing complex queries for {@link AuthSession} entities in the database.
 * The main input is a {@link AuthSessionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AuthSessionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AuthSessionQueryService extends QueryService<AuthSession> {

    private static final Logger LOG = LoggerFactory.getLogger(AuthSessionQueryService.class);

    private final AuthSessionRepository authSessionRepository;

    private final AuthSessionMapper authSessionMapper;

    public AuthSessionQueryService(AuthSessionRepository authSessionRepository, AuthSessionMapper authSessionMapper) {
        this.authSessionRepository = authSessionRepository;
        this.authSessionMapper = authSessionMapper;
    }

    /**
     * Return a {@link Page} of {@link AuthSessionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AuthSessionDTO> findByCriteria(AuthSessionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AuthSession> specification = createSpecification(criteria);
        return authSessionRepository.findAll(specification, page).map(authSessionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AuthSessionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<AuthSession> specification = createSpecification(criteria);
        return authSessionRepository.count(specification);
    }

    /**
     * Function to convert {@link AuthSessionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AuthSession> createSpecification(AuthSessionCriteria criteria) {
        Specification<AuthSession> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(AuthSession_.user, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), AuthSession_.id),
                    buildStringSpecification(criteria.getDeviceInfo(), AuthSession_.deviceInfo),
                    buildStringSpecification(criteria.getRefreshToken(), AuthSession_.refreshToken),
                    buildRangeSpecification(criteria.getExpiresAt(), AuthSession_.expiresAt),
                    buildSpecification(criteria.getIsRevoked(), AuthSession_.isRevoked),
                    buildRangeSpecification(criteria.getCreatedAt(), AuthSession_.createdAt),
                    buildSpecification(criteria.getUserId(), root -> root.join(AuthSession_.user, JoinType.LEFT).get(User_.id))
                )
            );
        }
        return specification;
    }
}
