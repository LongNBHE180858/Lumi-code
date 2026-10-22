package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.ProctorEvent;
import com.lumi.repository.ProctorEventRepository;
import com.lumi.service.criteria.ProctorEventCriteria;
import com.lumi.service.dto.ProctorEventDTO;
import com.lumi.service.mapper.ProctorEventMapper;
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
 * Service for executing complex queries for {@link ProctorEvent} entities in the database.
 * The main input is a {@link ProctorEventCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ProctorEventDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProctorEventQueryService extends QueryService<ProctorEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(ProctorEventQueryService.class);

    private final ProctorEventRepository proctorEventRepository;

    private final ProctorEventMapper proctorEventMapper;

    public ProctorEventQueryService(ProctorEventRepository proctorEventRepository, ProctorEventMapper proctorEventMapper) {
        this.proctorEventRepository = proctorEventRepository;
        this.proctorEventMapper = proctorEventMapper;
    }

    /**
     * Return a {@link Page} of {@link ProctorEventDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProctorEventDTO> findByCriteria(ProctorEventCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ProctorEvent> specification = createSpecification(criteria);
        return proctorEventRepository.findAll(specification, page).map(proctorEventMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProctorEventCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ProctorEvent> specification = createSpecification(criteria);
        return proctorEventRepository.count(specification);
    }

    /**
     * Function to convert {@link ProctorEventCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProctorEvent> createSpecification(ProctorEventCriteria criteria) {
        Specification<ProctorEvent> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(ProctorEvent_.session, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), ProctorEvent_.id),
                    buildStringSpecification(criteria.getViolationType(), ProctorEvent_.violationType),
                    buildStringSpecification(criteria.getPhotoUrl(), ProctorEvent_.photoUrl),
                    buildRangeSpecification(criteria.getOccurredAt(), ProctorEvent_.occurredAt),
                    buildSpecification(criteria.getSessionId(), root ->
                        root.join(ProctorEvent_.session, JoinType.LEFT).get(ProctorSession_.id)
                    )
                )
            );
        }
        return specification;
    }
}
