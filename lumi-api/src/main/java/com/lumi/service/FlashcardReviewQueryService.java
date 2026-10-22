package com.lumi.service;

import com.lumi.domain.*; // for static metamodels
import com.lumi.domain.FlashcardReview;
import com.lumi.repository.FlashcardReviewRepository;
import com.lumi.service.criteria.FlashcardReviewCriteria;
import com.lumi.service.dto.FlashcardReviewDTO;
import com.lumi.service.mapper.FlashcardReviewMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link FlashcardReview} entities in the database.
 * The main input is a {@link FlashcardReviewCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FlashcardReviewDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FlashcardReviewQueryService extends QueryService<FlashcardReview> {

    private static final Logger LOG = LoggerFactory.getLogger(FlashcardReviewQueryService.class);

    private final FlashcardReviewRepository flashcardReviewRepository;

    private final FlashcardReviewMapper flashcardReviewMapper;

    public FlashcardReviewQueryService(FlashcardReviewRepository flashcardReviewRepository, FlashcardReviewMapper flashcardReviewMapper) {
        this.flashcardReviewRepository = flashcardReviewRepository;
        this.flashcardReviewMapper = flashcardReviewMapper;
    }

    /**
     * Return a {@link List} of {@link FlashcardReviewDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FlashcardReviewDTO> findByCriteria(FlashcardReviewCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<FlashcardReview> specification = createSpecification(criteria);
        return flashcardReviewMapper.toDto(flashcardReviewRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FlashcardReviewCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<FlashcardReview> specification = createSpecification(criteria);
        return flashcardReviewRepository.count(specification);
    }

    /**
     * Function to convert {@link FlashcardReviewCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<FlashcardReview> createSpecification(FlashcardReviewCriteria criteria) {
        Specification<FlashcardReview> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(FlashcardReview_.student, JoinType.LEFT);
                root.fetch(FlashcardReview_.flashcard, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), FlashcardReview_.id),
                    buildRangeSpecification(criteria.getNextReviewAt(), FlashcardReview_.nextReviewAt),
                    buildRangeSpecification(criteria.getIntervalDays(), FlashcardReview_.intervalDays),
                    buildSpecification(criteria.getStudentId(), root -> root.join(FlashcardReview_.student, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getFlashcardId(), root ->
                        root.join(FlashcardReview_.flashcard, JoinType.LEFT).get(Flashcard_.id)
                    )
                )
            );
        }
        return specification;
    }
}
