package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.SystemPlan;
import com.lumi.repository.SystemPlanRepository;
import com.lumi.service.criteria.SystemPlanCriteria;
import com.lumi.service.dto.SystemPlanDTO;
import com.lumi.service.mapper.SystemPlanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link SystemPlan} entities in the database.
 * The main input is a {@link SystemPlanCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SystemPlanDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SystemPlanQueryService extends QueryService<SystemPlan> {

    private static final Logger LOG = LoggerFactory.getLogger(SystemPlanQueryService.class);

    private final SystemPlanRepository systemPlanRepository;

    private final SystemPlanMapper systemPlanMapper;

    public SystemPlanQueryService(SystemPlanRepository systemPlanRepository, SystemPlanMapper systemPlanMapper) {
        this.systemPlanRepository = systemPlanRepository;
        this.systemPlanMapper = systemPlanMapper;
    }

    /**
     * Return a {@link Page} of {@link SystemPlanDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SystemPlanDTO> findByCriteria(SystemPlanCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SystemPlan> specification = createSpecification(criteria);
        return systemPlanRepository.findAll(specification, page).map(systemPlanMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SystemPlanCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<SystemPlan> specification = createSpecification(criteria);
        return systemPlanRepository.count(specification);
    }

    /**
     * Function to convert {@link SystemPlanCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SystemPlan> createSpecification(SystemPlanCriteria criteria) {
        Specification<SystemPlan> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), SystemPlan_.id),
                    buildStringSpecification(criteria.getName(), SystemPlan_.name),
                    buildRangeSpecification(criteria.getPrice(), SystemPlan_.price),
                    buildRangeSpecification(criteria.getDurationDays(), SystemPlan_.durationDays),
                    buildRangeSpecification(criteria.getStorageQuotaMb(), SystemPlan_.storageQuotaMb),
                    buildRangeSpecification(criteria.getAiGenerationLimit(), SystemPlan_.aiGenerationLimit),
                    buildRangeSpecification(criteria.getMaxClasses(), SystemPlan_.maxClasses)
                )
            );
        }
        return specification;
    }
}
