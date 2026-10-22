package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.TeacherQuota;
import com.lumi.repository.TeacherQuotaRepository;
import com.lumi.service.criteria.TeacherQuotaCriteria;
import com.lumi.service.dto.TeacherQuotaDTO;
import com.lumi.service.mapper.TeacherQuotaMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TeacherQuota} entities in the database.
 * The main input is a {@link TeacherQuotaCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TeacherQuotaDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TeacherQuotaQueryService extends QueryService<TeacherQuota> {

    private static final Logger LOG = LoggerFactory.getLogger(TeacherQuotaQueryService.class);

    private final TeacherQuotaRepository teacherQuotaRepository;

    private final TeacherQuotaMapper teacherQuotaMapper;

    public TeacherQuotaQueryService(TeacherQuotaRepository teacherQuotaRepository, TeacherQuotaMapper teacherQuotaMapper) {
        this.teacherQuotaRepository = teacherQuotaRepository;
        this.teacherQuotaMapper = teacherQuotaMapper;
    }

    /**
     * Return a {@link List} of {@link TeacherQuotaDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TeacherQuotaDTO> findByCriteria(TeacherQuotaCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<TeacherQuota> specification = createSpecification(criteria);
        return teacherQuotaMapper.toDto(teacherQuotaRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TeacherQuotaCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TeacherQuota> specification = createSpecification(criteria);
        return teacherQuotaRepository.count(specification);
    }

    /**
     * Function to convert {@link TeacherQuotaCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TeacherQuota> createSpecification(TeacherQuotaCriteria criteria) {
        Specification<TeacherQuota> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(TeacherQuota_.user, JoinType.LEFT);
                root.fetch(TeacherQuota_.currentPlan, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), TeacherQuota_.id),
                    buildRangeSpecification(criteria.getPlanExpiresAt(), TeacherQuota_.planExpiresAt),
                    buildRangeSpecification(criteria.getUsedStorageBytes(), TeacherQuota_.usedStorageBytes),
                    buildRangeSpecification(criteria.getAiUsedCount(), TeacherQuota_.aiUsedCount),
                    buildRangeSpecification(criteria.getRowVersion(), TeacherQuota_.rowVersion),
                    buildSpecification(criteria.getUserId(), root -> root.join(TeacherQuota_.user, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getCurrentPlanId(), root ->
                        root.join(TeacherQuota_.currentPlan, JoinType.LEFT).get(SystemPlan_.id)
                    )
                )
            );
        }
        return specification;
    }
}
