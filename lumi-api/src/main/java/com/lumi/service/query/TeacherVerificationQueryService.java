package com.lumi.service.query;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.TeacherVerification;
import com.lumi.repository.TeacherVerificationRepository;
import com.lumi.service.criteria.TeacherVerificationCriteria;
import com.lumi.service.dto.TeacherVerificationDTO;
import com.lumi.service.mapper.TeacherVerificationMapper;
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
 * Service for executing complex queries for {@link TeacherVerification} entities in the database.
 * The main input is a {@link TeacherVerificationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TeacherVerificationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TeacherVerificationQueryService extends QueryService<TeacherVerification> {

    private static final Logger LOG = LoggerFactory.getLogger(TeacherVerificationQueryService.class);

    private final TeacherVerificationRepository teacherVerificationRepository;

    private final TeacherVerificationMapper teacherVerificationMapper;

    public TeacherVerificationQueryService(
        TeacherVerificationRepository teacherVerificationRepository,
        TeacherVerificationMapper teacherVerificationMapper
    ) {
        this.teacherVerificationRepository = teacherVerificationRepository;
        this.teacherVerificationMapper = teacherVerificationMapper;
    }

    /**
     * Return a {@link Page} of {@link TeacherVerificationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TeacherVerificationDTO> findByCriteria(TeacherVerificationCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TeacherVerification> specification = createSpecification(criteria);
        return teacherVerificationRepository.findAll(specification, page).map(teacherVerificationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TeacherVerificationCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TeacherVerification> specification = createSpecification(criteria);
        return teacherVerificationRepository.count(specification);
    }

    /**
     * Function to convert {@link TeacherVerificationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TeacherVerification> createSpecification(TeacherVerificationCriteria criteria) {
        Specification<TeacherVerification> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(TeacherVerification_.user, JoinType.LEFT);
                root.fetch(TeacherVerification_.reviewedBy, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), TeacherVerification_.id),
                    buildStringSpecification(criteria.getIdentityDocumentUrl(), TeacherVerification_.identityDocumentUrl),
                    buildStringSpecification(criteria.getVerificationStatus(), TeacherVerification_.verificationStatus),
                    buildRangeSpecification(criteria.getSubmittedAt(), TeacherVerification_.submittedAt),
                    buildRangeSpecification(criteria.getApprovedAt(), TeacherVerification_.approvedAt),
                    buildRangeSpecification(criteria.getReviewedAt(), TeacherVerification_.reviewedAt),
                    buildSpecification(criteria.getUserId(), root -> root.join(TeacherVerification_.user, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getReviewedById(), root ->
                        root.join(TeacherVerification_.reviewedBy, JoinType.LEFT).get(User_.id)
                    )
                )
            );
        }
        return specification;
    }
}
