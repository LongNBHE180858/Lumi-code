package com.lumi.service.query;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.InstructorWallet;
import com.lumi.repository.InstructorWalletRepository;
import com.lumi.service.criteria.InstructorWalletCriteria;
import com.lumi.service.dto.InstructorWalletDTO;
import com.lumi.service.mapper.InstructorWalletMapper;
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
 * Service for executing complex queries for {@link InstructorWallet} entities in the database.
 * The main input is a {@link InstructorWalletCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link InstructorWalletDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InstructorWalletQueryService extends QueryService<InstructorWallet> {

    private static final Logger LOG = LoggerFactory.getLogger(InstructorWalletQueryService.class);

    private final InstructorWalletRepository instructorWalletRepository;

    private final InstructorWalletMapper instructorWalletMapper;

    public InstructorWalletQueryService(
        InstructorWalletRepository instructorWalletRepository,
        InstructorWalletMapper instructorWalletMapper
    ) {
        this.instructorWalletRepository = instructorWalletRepository;
        this.instructorWalletMapper = instructorWalletMapper;
    }

    /**
     * Return a {@link Page} of {@link InstructorWalletDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<InstructorWalletDTO> findByCriteria(InstructorWalletCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<InstructorWallet> specification = createSpecification(criteria);
        return instructorWalletRepository.findAll(specification, page).map(instructorWalletMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InstructorWalletCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<InstructorWallet> specification = createSpecification(criteria);
        return instructorWalletRepository.count(specification);
    }

    /**
     * Function to convert {@link InstructorWalletCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<InstructorWallet> createSpecification(InstructorWalletCriteria criteria) {
        Specification<InstructorWallet> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(InstructorWallet_.instructor, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), InstructorWallet_.id),
                    buildRangeSpecification(criteria.getPendingAmount(), InstructorWallet_.pendingAmount),
                    buildRangeSpecification(criteria.getAvailableAmount(), InstructorWallet_.availableAmount),
                    buildRangeSpecification(criteria.getRowVersion(), InstructorWallet_.rowVersion),
                    buildSpecification(criteria.getInstructorId(), root ->
                        root.join(InstructorWallet_.instructor, JoinType.LEFT).get(User_.id)
                    )
                )
            );
        }
        return specification;
    }
}
