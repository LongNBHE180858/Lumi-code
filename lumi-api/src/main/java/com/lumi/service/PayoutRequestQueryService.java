package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.PayoutRequest;
import com.lumi.repository.PayoutRequestRepository;
import com.lumi.service.criteria.PayoutRequestCriteria;
import com.lumi.service.dto.PayoutRequestDTO;
import com.lumi.service.mapper.PayoutRequestMapper;
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
 * Service for executing complex queries for {@link PayoutRequest} entities in the database.
 * The main input is a {@link PayoutRequestCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PayoutRequestDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PayoutRequestQueryService extends QueryService<PayoutRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(PayoutRequestQueryService.class);

    private final PayoutRequestRepository payoutRequestRepository;

    private final PayoutRequestMapper payoutRequestMapper;

    public PayoutRequestQueryService(PayoutRequestRepository payoutRequestRepository, PayoutRequestMapper payoutRequestMapper) {
        this.payoutRequestRepository = payoutRequestRepository;
        this.payoutRequestMapper = payoutRequestMapper;
    }

    /**
     * Return a {@link Page} of {@link PayoutRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PayoutRequestDTO> findByCriteria(PayoutRequestCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PayoutRequest> specification = createSpecification(criteria);
        return payoutRequestRepository.findAll(specification, page).map(payoutRequestMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PayoutRequestCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<PayoutRequest> specification = createSpecification(criteria);
        return payoutRequestRepository.count(specification);
    }

    /**
     * Function to convert {@link PayoutRequestCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PayoutRequest> createSpecification(PayoutRequestCriteria criteria) {
        Specification<PayoutRequest> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(PayoutRequest_.wallet, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), PayoutRequest_.id),
                    buildRangeSpecification(criteria.getAmount(), PayoutRequest_.amount),
                    buildStringSpecification(criteria.getBankCode(), PayoutRequest_.bankCode),
                    buildStringSpecification(criteria.getBankAccount(), PayoutRequest_.bankAccount),
                    buildStringSpecification(criteria.getStatus(), PayoutRequest_.status),
                    buildRangeSpecification(criteria.getCreatedAt(), PayoutRequest_.createdAt),
                    buildSpecification(criteria.getWalletId(), root ->
                        root.join(PayoutRequest_.wallet, JoinType.LEFT).get(InstructorWallet_.id)
                    )
                )
            );
        }
        return specification;
    }
}
