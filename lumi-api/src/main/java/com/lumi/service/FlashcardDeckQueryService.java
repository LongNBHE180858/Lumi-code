package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.FlashcardDeck;
import com.lumi.repository.FlashcardDeckRepository;
import com.lumi.service.criteria.FlashcardDeckCriteria;
import com.lumi.service.dto.FlashcardDeckDTO;
import com.lumi.service.mapper.FlashcardDeckMapper;
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
 * Service for executing complex queries for {@link FlashcardDeck} entities in the database.
 * The main input is a {@link FlashcardDeckCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link FlashcardDeckDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FlashcardDeckQueryService extends QueryService<FlashcardDeck> {

    private static final Logger LOG = LoggerFactory.getLogger(FlashcardDeckQueryService.class);

    private final FlashcardDeckRepository flashcardDeckRepository;

    private final FlashcardDeckMapper flashcardDeckMapper;

    public FlashcardDeckQueryService(FlashcardDeckRepository flashcardDeckRepository, FlashcardDeckMapper flashcardDeckMapper) {
        this.flashcardDeckRepository = flashcardDeckRepository;
        this.flashcardDeckMapper = flashcardDeckMapper;
    }

    /**
     * Return a {@link Page} of {@link FlashcardDeckDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FlashcardDeckDTO> findByCriteria(FlashcardDeckCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<FlashcardDeck> specification = createSpecification(criteria);
        return flashcardDeckRepository.findAll(specification, page).map(flashcardDeckMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FlashcardDeckCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<FlashcardDeck> specification = createSpecification(criteria);
        return flashcardDeckRepository.count(specification);
    }

    /**
     * Function to convert {@link FlashcardDeckCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<FlashcardDeck> createSpecification(FlashcardDeckCriteria criteria) {
        Specification<FlashcardDeck> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(FlashcardDeck_.owner, JoinType.LEFT);
                root.fetch(FlashcardDeck_.subject, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), FlashcardDeck_.id),
                    buildStringSpecification(criteria.getTitle(), FlashcardDeck_.title),
                    buildSpecification(criteria.getIsPublic(), FlashcardDeck_.isPublic),
                    buildSpecification(criteria.getOwnerId(), root -> root.join(FlashcardDeck_.owner, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getSubjectId(), root -> root.join(FlashcardDeck_.subject, JoinType.LEFT).get(Subject_.id))
                )
            );
        }
        return specification;
    }
}
