package com.lumi.web.rest;

import com.lumi.repository.PdfChunkRepository;
import com.lumi.service.PdfChunkService;
import com.lumi.service.criteria.PdfChunkCriteria;
import com.lumi.service.dto.PdfChunkDTO;
import com.lumi.service.query.PdfChunkQueryService;
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
 * REST controller for managing {@link com.lumi.domain.PdfChunk}.
 */
@RestController
@RequestMapping("/api/pdf-chunks")
public class PdfChunkResource {

    private static final Logger LOG = LoggerFactory.getLogger(PdfChunkResource.class);

    private static final String ENTITY_NAME = "pdfChunk";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final PdfChunkService pdfChunkService;

    private final PdfChunkRepository pdfChunkRepository;

    private final PdfChunkQueryService pdfChunkQueryService;

    public PdfChunkResource(
        PdfChunkService pdfChunkService,
        PdfChunkRepository pdfChunkRepository,
        PdfChunkQueryService pdfChunkQueryService
    ) {
        this.pdfChunkService = pdfChunkService;
        this.pdfChunkRepository = pdfChunkRepository;
        this.pdfChunkQueryService = pdfChunkQueryService;
    }

    /**
     * {@code POST  /pdf-chunks} : Create a new pdfChunk.
     *
     * @param pdfChunkDTO the pdfChunkDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pdfChunkDTO, or with status {@code 400 (Bad Request)} if the pdfChunk already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PdfChunkDTO> createPdfChunk(@Valid @RequestBody PdfChunkDTO pdfChunkDTO) throws URISyntaxException {
        LOG.debug("REST request to save PdfChunk : {}", pdfChunkDTO);
        if (pdfChunkDTO.getId() != null) {
            throw new BadRequestAlertException("A new pdfChunk cannot already have an ID", ENTITY_NAME, "idexists");
        }
        pdfChunkDTO = pdfChunkService.save(pdfChunkDTO);
        return ResponseEntity.created(new URI("/api/pdf-chunks/" + pdfChunkDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, pdfChunkDTO.getId().toString()))
            .body(pdfChunkDTO);
    }

    /**
     * {@code PUT  /pdf-chunks/:id} : Updates an existing pdfChunk.
     *
     * @param id the id of the pdfChunkDTO to save.
     * @param pdfChunkDTO the pdfChunkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pdfChunkDTO,
     * or with status {@code 400 (Bad Request)} if the pdfChunkDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pdfChunkDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PdfChunkDTO> updatePdfChunk(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PdfChunkDTO pdfChunkDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PdfChunk : {}, {}", id, pdfChunkDTO);
        if (pdfChunkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pdfChunkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pdfChunkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        pdfChunkDTO = pdfChunkService.update(pdfChunkDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pdfChunkDTO.getId().toString()))
            .body(pdfChunkDTO);
    }

    /**
     * {@code PATCH  /pdf-chunks/:id} : Partial updates given fields of an existing pdfChunk, field will ignore if it is null
     *
     * @param id the id of the pdfChunkDTO to save.
     * @param pdfChunkDTO the pdfChunkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pdfChunkDTO,
     * or with status {@code 400 (Bad Request)} if the pdfChunkDTO is not valid,
     * or with status {@code 404 (Not Found)} if the pdfChunkDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the pdfChunkDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PdfChunkDTO> partialUpdatePdfChunk(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PdfChunkDTO pdfChunkDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update PdfChunk : {}, {}", id, pdfChunkDTO);
        if (pdfChunkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pdfChunkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pdfChunkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PdfChunkDTO> result = pdfChunkService.partialUpdate(pdfChunkDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pdfChunkDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /pdf-chunks} : get all the Pdf Chunks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Pdf Chunks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PdfChunkDTO>> getAllPdfChunks(
        PdfChunkCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get PdfChunks by criteria: {}", criteria);

        Page<PdfChunkDTO> page = pdfChunkQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /pdf-chunks/count} : count all the pdfChunks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPdfChunks(PdfChunkCriteria criteria) {
        LOG.debug("REST request to count PdfChunks by criteria: {}", criteria);
        return ResponseEntity.ok().body(pdfChunkQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /pdf-chunks/:id} : get the "id" pdfChunk.
     *
     * @param id the id of the pdfChunkDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pdfChunkDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PdfChunkDTO> getPdfChunk(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PdfChunk : {}", id);
        Optional<PdfChunkDTO> pdfChunkDTO = pdfChunkService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pdfChunkDTO);
    }

    /**
     * {@code DELETE  /pdf-chunks/:id} : delete the "id" pdfChunk.
     *
     * @param id the id of the pdfChunkDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePdfChunk(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PdfChunk : {}", id);
        pdfChunkService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
