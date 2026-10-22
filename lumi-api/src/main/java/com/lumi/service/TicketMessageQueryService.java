package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.TicketMessage;
import com.lumi.repository.TicketMessageRepository;
import com.lumi.service.criteria.TicketMessageCriteria;
import com.lumi.service.dto.TicketMessageDTO;
import com.lumi.service.mapper.TicketMessageMapper;
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
 * Service for executing complex queries for {@link TicketMessage} entities in the database.
 * The main input is a {@link TicketMessageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TicketMessageDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TicketMessageQueryService extends QueryService<TicketMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(TicketMessageQueryService.class);

    private final TicketMessageRepository ticketMessageRepository;

    private final TicketMessageMapper ticketMessageMapper;

    public TicketMessageQueryService(TicketMessageRepository ticketMessageRepository, TicketMessageMapper ticketMessageMapper) {
        this.ticketMessageRepository = ticketMessageRepository;
        this.ticketMessageMapper = ticketMessageMapper;
    }

    /**
     * Return a {@link Page} of {@link TicketMessageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TicketMessageDTO> findByCriteria(TicketMessageCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TicketMessage> specification = createSpecification(criteria);
        return ticketMessageRepository.findAll(specification, page).map(ticketMessageMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TicketMessageCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TicketMessage> specification = createSpecification(criteria);
        return ticketMessageRepository.count(specification);
    }

    /**
     * Function to convert {@link TicketMessageCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TicketMessage> createSpecification(TicketMessageCriteria criteria) {
        Specification<TicketMessage> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(TicketMessage_.sender, JoinType.LEFT);
                root.fetch(TicketMessage_.ticket, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), TicketMessage_.id),
                    buildStringSpecification(criteria.getContent(), TicketMessage_.content),
                    buildRangeSpecification(criteria.getCreatedAt(), TicketMessage_.createdAt),
                    buildSpecification(criteria.getSenderId(), root -> root.join(TicketMessage_.sender, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getTicketId(), root ->
                        root.join(TicketMessage_.ticket, JoinType.LEFT).get(SupportTicket_.id)
                    )
                )
            );
        }
        return specification;
    }
}
