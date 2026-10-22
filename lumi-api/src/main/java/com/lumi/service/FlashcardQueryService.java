package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.Flashcard;
import com.lumi.repository.FlashcardRepository;
import com.lumi.service.criteria.FlashcardCriteria;
import com.lumi.service.dto.FlashcardDTO;
import com.lumi.service.mapper.FlashcardMapper;
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
 * Service for executing complex queries for {@link Flashcard} entities in the database.
 * The main input is a {@link FlashcardCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link FlashcardDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FlashcardQueryService extends QueryService<Flashcard> {

    private static final Logger LOG = LoggerFactory.getLogger(FlashcardQueryService.class);

    private final FlashcardRepository flashcardRepository;

    private final FlashcardMapper flashcardMapper;

    public FlashcardQueryService(FlashcardRepository flashcardRepository, FlashcardMapper flashcardMapper) {
        this.flashcardRepository = flashcardRepository;
        this.flashcardMapper = flashcardMapper;
    }

    /**
     * Return a {@link Page} of {@link FlashcardDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FlashcardDTO> findByCriteria(FlashcardCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Flashcard> specification = createSpecification(criteria);
        return flashcardRepository.findAll(specification, page).map(flashcardMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FlashcardCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Flashcard> specification = createSpecification(criteria);
        return flashcardRepository.count(specification);
    }

    /**
     * Function to convert {@link FlashcardCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Flashcard> createSpecification(FlashcardCriteria criteria) {
        Specification<Flashcard> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Flashcard_.deck, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Flashcard_.id),
                    buildStringSpecification(criteria.getFrontText(), Flashcard_.frontText),
                    buildStringSpecification(criteria.getBackText(), Flashcard_.backText),
                    buildSpecification(criteria.getDeckId(), root -> root.join(Flashcard_.deck, JoinType.LEFT).get(FlashcardDeck_.id))
                )
            );
        }
        return specification;
    }
}
