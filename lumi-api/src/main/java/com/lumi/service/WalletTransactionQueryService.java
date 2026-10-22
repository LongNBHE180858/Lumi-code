package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.WalletTransaction;
import com.lumi.repository.WalletTransactionRepository;
import com.lumi.service.criteria.WalletTransactionCriteria;
import com.lumi.service.dto.WalletTransactionDTO;
import com.lumi.service.mapper.WalletTransactionMapper;
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
 * Service for executing complex queries for {@link WalletTransaction} entities in the database.
 * The main input is a {@link WalletTransactionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link WalletTransactionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WalletTransactionQueryService extends QueryService<WalletTransaction> {

    private static final Logger LOG = LoggerFactory.getLogger(WalletTransactionQueryService.class);

    private final WalletTransactionRepository walletTransactionRepository;

    private final WalletTransactionMapper walletTransactionMapper;

    public WalletTransactionQueryService(
        WalletTransactionRepository walletTransactionRepository,
        WalletTransactionMapper walletTransactionMapper
    ) {
        this.walletTransactionRepository = walletTransactionRepository;
        this.walletTransactionMapper = walletTransactionMapper;
    }

    /**
     * Return a {@link Page} of {@link WalletTransactionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WalletTransactionDTO> findByCriteria(WalletTransactionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<WalletTransaction> specification = createSpecification(criteria);
        return walletTransactionRepository.findAll(specification, page).map(walletTransactionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WalletTransactionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<WalletTransaction> specification = createSpecification(criteria);
        return walletTransactionRepository.count(specification);
    }

    /**
     * Function to convert {@link WalletTransactionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<WalletTransaction> createSpecification(WalletTransactionCriteria criteria) {
        Specification<WalletTransaction> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(WalletTransaction_.wallet, JoinType.LEFT);
                root.fetch(WalletTransaction_.order, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), WalletTransaction_.id),
                    buildSpecification(criteria.getType(), WalletTransaction_.type),
                    buildRangeSpecification(criteria.getAmount(), WalletTransaction_.amount),
                    buildStringSpecification(criteria.getStatus(), WalletTransaction_.status),
                    buildStringSpecification(criteria.getIdempotencyKey(), WalletTransaction_.idempotencyKey),
                    buildRangeSpecification(criteria.getOccurredAt(), WalletTransaction_.occurredAt),
                    buildSpecification(criteria.getWalletId(), root ->
                        root.join(WalletTransaction_.wallet, JoinType.LEFT).get(InstructorWallet_.id)
                    ),
                    buildSpecification(criteria.getOrderId(), root ->
                        root.join(WalletTransaction_.order, JoinType.LEFT).get(CourseOrder_.id)
                    )
                )
            );
        }
        return specification;
    }
}
