package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.UserProfile;
import com.lumi.repository.UserProfileRepository;
import com.lumi.service.criteria.UserProfileCriteria;
import com.lumi.service.dto.UserProfileDTO;
import com.lumi.service.mapper.UserProfileMapper;
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
 * Service for executing complex queries for {@link UserProfile} entities in the database.
 * The main input is a {@link UserProfileCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link UserProfileDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserProfileQueryService extends QueryService<UserProfile> {

    private static final Logger LOG = LoggerFactory.getLogger(UserProfileQueryService.class);

    private final UserProfileRepository userProfileRepository;

    private final UserProfileMapper userProfileMapper;

    public UserProfileQueryService(UserProfileRepository userProfileRepository, UserProfileMapper userProfileMapper) {
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
    }

    /**
     * Return a {@link Page} of {@link UserProfileDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UserProfileDTO> findByCriteria(UserProfileCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UserProfile> specification = createSpecification(criteria);
        return userProfileRepository.findAll(specification, page).map(userProfileMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserProfileCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<UserProfile> specification = createSpecification(criteria);
        return userProfileRepository.count(specification);
    }

    /**
     * Function to convert {@link UserProfileCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UserProfile> createSpecification(UserProfileCriteria criteria) {
        Specification<UserProfile> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(UserProfile_.user, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), UserProfile_.id),
                    buildStringSpecification(criteria.getPhoneNumber(), UserProfile_.phoneNumber),
                    buildStringSpecification(criteria.getBio(), UserProfile_.bio),
                    buildStringSpecification(criteria.getSchoolName(), UserProfile_.schoolName),
                    buildRangeSpecification(criteria.getGradeLevel(), UserProfile_.gradeLevel),
                    buildSpecification(criteria.getIs2faEnabled(), UserProfile_.is2faEnabled),
                    buildSpecification(criteria.getInstructorStatus(), UserProfile_.instructorStatus),
                    buildRangeSpecification(criteria.getUpdatedAt(), UserProfile_.updatedAt),
                    buildSpecification(criteria.getUserId(), root -> root.join(UserProfile_.user, JoinType.LEFT).get(User_.id))
                )
            );
        }
        return specification;
    }
}
