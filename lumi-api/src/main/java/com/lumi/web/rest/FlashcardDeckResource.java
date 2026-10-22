package com.lumi.web.rest;

import com.lumi.repository.FlashcardDeckRepository;
import com.lumi.service.FlashcardDeckQueryService;
import com.lumi.service.FlashcardDeckService;
import com.lumi.service.criteria.FlashcardDeckCriteria;
import com.lumi.service.dto.FlashcardDeckDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.lumi.domain.FlashcardDeck}.
 */
@RestController
@RequestMapping("/api/flashcard-decks")
public class FlashcardDeckResource {

    private static final Logger LOG = LoggerFactory.getLogger(FlashcardDeckResource.class);

    private static final String ENTITY_NAME = "flashcardDeck";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final FlashcardDeckService flashcardDeckService;

    private final FlashcardDeckRepository flashcardDeckRepository;

    private final FlashcardDeckQueryService flashcardDeckQueryService;

    public FlashcardDeckResource(
        FlashcardDeckService flashcardDeckService,
        FlashcardDeckRepository flashcardDeckRepository,
        FlashcardDeckQueryService flashcardDeckQueryService
    ) {
        this.flashcardDeckService = flashcardDeckService;
        this.flashcardDeckRepository = flashcardDeckRepository;
        this.flashcardDeckQueryService = flashcardDeckQueryService;
    }

    /**
     * {@code POST  /flashcard-decks} : Create a new flashcardDeck.
     *
     * @param flashcardDeckDTO the flashcardDeckDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new flashcardDeckDTO, or with status {@code 400 (Bad Request)} if the flashcardDeck already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FlashcardDeckDTO> createFlashcardDeck(@Valid @RequestBody FlashcardDeckDTO flashcardDeckDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save FlashcardDeck : {}", flashcardDeckDTO);
        if (flashcardDeckDTO.getId() != null) {
            throw new BadRequestAlertException("A new flashcardDeck cannot already have an ID", ENTITY_NAME, "idexists");
        }
        flashcardDeckDTO = flashcardDeckService.save(flashcardDeckDTO);
        return ResponseEntity.created(new URI("/api/flashcard-decks/" + flashcardDeckDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, flashcardDeckDTO.getId().toString()))
            .body(flashcardDeckDTO);
    }

    /**
     * {@code PUT  /flashcard-decks/:id} : Updates an existing flashcardDeck.
     *
     * @param id the id of the flashcardDeckDTO to save.
     * @param flashcardDeckDTO the flashcardDeckDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated flashcardDeckDTO,
     * or with status {@code 400 (Bad Request)} if the flashcardDeckDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the flashcardDeckDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FlashcardDeckDTO> updateFlashcardDeck(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FlashcardDeckDTO flashcardDeckDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update FlashcardDeck : {}, {}", id, flashcardDeckDTO);
        if (flashcardDeckDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, flashcardDeckDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!flashcardDeckRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        flashcardDeckDTO = flashcardDeckService.update(flashcardDeckDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, flashcardDeckDTO.getId().toString()))
            .body(flashcardDeckDTO);
    }

    /**
     * {@code PATCH  /flashcard-decks/:id} : Partial updates given fields of an existing flashcardDeck, field will ignore if it is null
     *
     * @param id the id of the flashcardDeckDTO to save.
     * @param flashcardDeckDTO the flashcardDeckDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated flashcardDeckDTO,
     * or with status {@code 400 (Bad Request)} if the flashcardDeckDTO is not valid,
     * or with status {@code 404 (Not Found)} if the flashcardDeckDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the flashcardDeckDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FlashcardDeckDTO> partialUpdateFlashcardDeck(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FlashcardDeckDTO flashcardDeckDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update FlashcardDeck : {}, {}", id, flashcardDeckDTO);
        if (flashcardDeckDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, flashcardDeckDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!flashcardDeckRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FlashcardDeckDTO> result = flashcardDeckService.partialUpdate(flashcardDeckDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, flashcardDeckDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /flashcard-decks} : get all the Flashcard Decks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Flashcard Decks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<FlashcardDeckDTO>> getAllFlashcardDecks(
        FlashcardDeckCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get FlashcardDecks by criteria: {}", criteria);

        Page<FlashcardDeckDTO> page = flashcardDeckQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /flashcard-decks/count} : count all the flashcardDecks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countFlashcardDecks(FlashcardDeckCriteria criteria) {
        LOG.debug("REST request to count FlashcardDecks by criteria: {}", criteria);
        return ResponseEntity.ok().body(flashcardDeckQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /flashcard-decks/:id} : get the "id" flashcardDeck.
     *
     * @param id the id of the flashcardDeckDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the flashcardDeckDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FlashcardDeckDTO> getFlashcardDeck(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FlashcardDeck : {}", id);
        Optional<FlashcardDeckDTO> flashcardDeckDTO = flashcardDeckService.findOne(id);
        return ResponseUtil.wrapOrNotFound(flashcardDeckDTO);
    }

    /**
     * {@code DELETE  /flashcard-decks/:id} : delete the "id" flashcardDeck.
     *
     * @param id the id of the flashcardDeckDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlashcardDeck(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FlashcardDeck : {}", id);
        flashcardDeckService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
