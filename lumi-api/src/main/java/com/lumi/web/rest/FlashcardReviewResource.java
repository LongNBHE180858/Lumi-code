package com.lumi.web.rest;

import com.lumi.repository.FlashcardReviewRepository;
import com.lumi.service.FlashcardReviewQueryService;
import com.lumi.service.FlashcardReviewService;
import com.lumi.service.criteria.FlashcardReviewCriteria;
import com.lumi.service.dto.FlashcardReviewDTO;
import com.lumi.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.lumi.domain.FlashcardReview}.
 */
@RestController
@RequestMapping("/api/flashcard-reviews")
public class FlashcardReviewResource {

    private static final Logger LOG = LoggerFactory.getLogger(FlashcardReviewResource.class);

    private static final String ENTITY_NAME = "flashcardReview";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final FlashcardReviewService flashcardReviewService;

    private final FlashcardReviewRepository flashcardReviewRepository;

    private final FlashcardReviewQueryService flashcardReviewQueryService;

    public FlashcardReviewResource(
        FlashcardReviewService flashcardReviewService,
        FlashcardReviewRepository flashcardReviewRepository,
        FlashcardReviewQueryService flashcardReviewQueryService
    ) {
        this.flashcardReviewService = flashcardReviewService;
        this.flashcardReviewRepository = flashcardReviewRepository;
        this.flashcardReviewQueryService = flashcardReviewQueryService;
    }

    /**
     * {@code POST  /flashcard-reviews} : Create a new flashcardReview.
     *
     * @param flashcardReviewDTO the flashcardReviewDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new flashcardReviewDTO, or with status {@code 400 (Bad Request)} if the flashcardReview already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FlashcardReviewDTO> createFlashcardReview(@Valid @RequestBody FlashcardReviewDTO flashcardReviewDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save FlashcardReview : {}", flashcardReviewDTO);
        if (flashcardReviewDTO.getId() != null) {
            throw new BadRequestAlertException("A new flashcardReview cannot already have an ID", ENTITY_NAME, "idexists");
        }
        flashcardReviewDTO = flashcardReviewService.save(flashcardReviewDTO);
        return ResponseEntity.created(new URI("/api/flashcard-reviews/" + flashcardReviewDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, flashcardReviewDTO.getId().toString()))
            .body(flashcardReviewDTO);
    }

    /**
     * {@code PUT  /flashcard-reviews/:id} : Updates an existing flashcardReview.
     *
     * @param id the id of the flashcardReviewDTO to save.
     * @param flashcardReviewDTO the flashcardReviewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated flashcardReviewDTO,
     * or with status {@code 400 (Bad Request)} if the flashcardReviewDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the flashcardReviewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FlashcardReviewDTO> updateFlashcardReview(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FlashcardReviewDTO flashcardReviewDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update FlashcardReview : {}, {}", id, flashcardReviewDTO);
        if (flashcardReviewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, flashcardReviewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!flashcardReviewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        flashcardReviewDTO = flashcardReviewService.update(flashcardReviewDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, flashcardReviewDTO.getId().toString()))
            .body(flashcardReviewDTO);
    }

    /**
     * {@code PATCH  /flashcard-reviews/:id} : Partial updates given fields of an existing flashcardReview, field will ignore if it is null
     *
     * @param id the id of the flashcardReviewDTO to save.
     * @param flashcardReviewDTO the flashcardReviewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated flashcardReviewDTO,
     * or with status {@code 400 (Bad Request)} if the flashcardReviewDTO is not valid,
     * or with status {@code 404 (Not Found)} if the flashcardReviewDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the flashcardReviewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FlashcardReviewDTO> partialUpdateFlashcardReview(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FlashcardReviewDTO flashcardReviewDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update FlashcardReview : {}, {}", id, flashcardReviewDTO);
        if (flashcardReviewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, flashcardReviewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!flashcardReviewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FlashcardReviewDTO> result = flashcardReviewService.partialUpdate(flashcardReviewDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, flashcardReviewDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /flashcard-reviews} : get all the Flashcard Reviews.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Flashcard Reviews in body.
     */
    @GetMapping("")
    public ResponseEntity<List<FlashcardReviewDTO>> getAllFlashcardReviews(FlashcardReviewCriteria criteria) {
        LOG.debug("REST request to get FlashcardReviews by criteria: {}", criteria);

        List<FlashcardReviewDTO> entityList = flashcardReviewQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /flashcard-reviews/count} : count all the flashcardReviews.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countFlashcardReviews(FlashcardReviewCriteria criteria) {
        LOG.debug("REST request to count FlashcardReviews by criteria: {}", criteria);
        return ResponseEntity.ok().body(flashcardReviewQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /flashcard-reviews/:id} : get the "id" flashcardReview.
     *
     * @param id the id of the flashcardReviewDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the flashcardReviewDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FlashcardReviewDTO> getFlashcardReview(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FlashcardReview : {}", id);
        Optional<FlashcardReviewDTO> flashcardReviewDTO = flashcardReviewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(flashcardReviewDTO);
    }

    /**
     * {@code DELETE  /flashcard-reviews/:id} : delete the "id" flashcardReview.
     *
     * @param id the id of the flashcardReviewDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlashcardReview(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FlashcardReview : {}", id);
        flashcardReviewService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
