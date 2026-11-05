package com.lumi.web.rest;

import com.lumi.repository.LearningDocumentRepository;
import com.lumi.service.LearningDocumentService;
import com.lumi.service.criteria.LearningDocumentCriteria;
import com.lumi.service.dto.LearningDocumentDTO;
import com.lumi.service.query.LearningDocumentQueryService;
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
 * REST controller for managing {@link com.lumi.domain.LearningDocument}.
 */
@RestController
@RequestMapping("/api/learning-documents")
public class LearningDocumentResource {

    private static final Logger LOG = LoggerFactory.getLogger(LearningDocumentResource.class);

    private static final String ENTITY_NAME = "learningDocument";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final LearningDocumentService learningDocumentService;

    private final LearningDocumentRepository learningDocumentRepository;

    private final LearningDocumentQueryService learningDocumentQueryService;

    public LearningDocumentResource(
        LearningDocumentService learningDocumentService,
        LearningDocumentRepository learningDocumentRepository,
        LearningDocumentQueryService learningDocumentQueryService
    ) {
        this.learningDocumentService = learningDocumentService;
        this.learningDocumentRepository = learningDocumentRepository;
        this.learningDocumentQueryService = learningDocumentQueryService;
    }

    /**
     * {@code POST  /learning-documents} : Create a new learningDocument.
     *
     * @param learningDocumentDTO the learningDocumentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new learningDocumentDTO, or with status {@code 400 (Bad Request)} if the learningDocument already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LearningDocumentDTO> createLearningDocument(@Valid @RequestBody LearningDocumentDTO learningDocumentDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save LearningDocument : {}", learningDocumentDTO);
        if (learningDocumentDTO.getId() != null) {
            throw new BadRequestAlertException("A new learningDocument cannot already have an ID", ENTITY_NAME, "idexists");
        }
        learningDocumentDTO = learningDocumentService.save(learningDocumentDTO);
        return ResponseEntity.created(new URI("/api/learning-documents/" + learningDocumentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, learningDocumentDTO.getId().toString()))
            .body(learningDocumentDTO);
    }

    /**
     * {@code PUT  /learning-documents/:id} : Updates an existing learningDocument.
     *
     * @param id the id of the learningDocumentDTO to save.
     * @param learningDocumentDTO the learningDocumentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated learningDocumentDTO,
     * or with status {@code 400 (Bad Request)} if the learningDocumentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the learningDocumentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LearningDocumentDTO> updateLearningDocument(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LearningDocumentDTO learningDocumentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update LearningDocument : {}, {}", id, learningDocumentDTO);
        if (learningDocumentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, learningDocumentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!learningDocumentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        learningDocumentDTO = learningDocumentService.update(learningDocumentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, learningDocumentDTO.getId().toString()))
            .body(learningDocumentDTO);
    }

    /**
     * {@code PATCH  /learning-documents/:id} : Partial updates given fields of an existing learningDocument, field will ignore if it is null
     *
     * @param id the id of the learningDocumentDTO to save.
     * @param learningDocumentDTO the learningDocumentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated learningDocumentDTO,
     * or with status {@code 400 (Bad Request)} if the learningDocumentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the learningDocumentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the learningDocumentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LearningDocumentDTO> partialUpdateLearningDocument(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LearningDocumentDTO learningDocumentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update LearningDocument : {}, {}", id, learningDocumentDTO);
        if (learningDocumentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, learningDocumentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!learningDocumentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LearningDocumentDTO> result = learningDocumentService.partialUpdate(learningDocumentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, learningDocumentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /learning-documents} : get all the Learning Documents.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Learning Documents in body.
     */
    @GetMapping("")
    public ResponseEntity<List<LearningDocumentDTO>> getAllLearningDocuments(
        LearningDocumentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get LearningDocuments by criteria: {}", criteria);

        Page<LearningDocumentDTO> page = learningDocumentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /learning-documents/count} : count all the learningDocuments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countLearningDocuments(LearningDocumentCriteria criteria) {
        LOG.debug("REST request to count LearningDocuments by criteria: {}", criteria);
        return ResponseEntity.ok().body(learningDocumentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /learning-documents/:id} : get the "id" learningDocument.
     *
     * @param id the id of the learningDocumentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the learningDocumentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LearningDocumentDTO> getLearningDocument(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LearningDocument : {}", id);
        Optional<LearningDocumentDTO> learningDocumentDTO = learningDocumentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(learningDocumentDTO);
    }

    /**
     * {@code DELETE  /learning-documents/:id} : delete the "id" learningDocument.
     *
     * @param id the id of the learningDocumentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLearningDocument(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LearningDocument : {}", id);
        learningDocumentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
