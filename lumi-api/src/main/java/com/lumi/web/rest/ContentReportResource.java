package com.lumi.web.rest;

import com.lumi.repository.ContentReportRepository;
import com.lumi.service.ContentReportQueryService;
import com.lumi.service.ContentReportService;
import com.lumi.service.criteria.ContentReportCriteria;
import com.lumi.service.dto.ContentReportDTO;
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
 * REST controller for managing {@link com.lumi.domain.ContentReport}.
 */
@RestController
@RequestMapping("/api/content-reports")
public class ContentReportResource {

    private static final Logger LOG = LoggerFactory.getLogger(ContentReportResource.class);

    private static final String ENTITY_NAME = "contentReport";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final ContentReportService contentReportService;

    private final ContentReportRepository contentReportRepository;

    private final ContentReportQueryService contentReportQueryService;

    public ContentReportResource(
        ContentReportService contentReportService,
        ContentReportRepository contentReportRepository,
        ContentReportQueryService contentReportQueryService
    ) {
        this.contentReportService = contentReportService;
        this.contentReportRepository = contentReportRepository;
        this.contentReportQueryService = contentReportQueryService;
    }

    /**
     * {@code POST  /content-reports} : Create a new contentReport.
     *
     * @param contentReportDTO the contentReportDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new contentReportDTO, or with status {@code 400 (Bad Request)} if the contentReport already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ContentReportDTO> createContentReport(@Valid @RequestBody ContentReportDTO contentReportDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ContentReport : {}", contentReportDTO);
        if (contentReportDTO.getId() != null) {
            throw new BadRequestAlertException("A new contentReport cannot already have an ID", ENTITY_NAME, "idexists");
        }
        contentReportDTO = contentReportService.save(contentReportDTO);
        return ResponseEntity.created(new URI("/api/content-reports/" + contentReportDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, contentReportDTO.getId().toString()))
            .body(contentReportDTO);
    }

    /**
     * {@code PUT  /content-reports/:id} : Updates an existing contentReport.
     *
     * @param id the id of the contentReportDTO to save.
     * @param contentReportDTO the contentReportDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contentReportDTO,
     * or with status {@code 400 (Bad Request)} if the contentReportDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the contentReportDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ContentReportDTO> updateContentReport(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ContentReportDTO contentReportDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ContentReport : {}, {}", id, contentReportDTO);
        if (contentReportDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, contentReportDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!contentReportRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        contentReportDTO = contentReportService.update(contentReportDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contentReportDTO.getId().toString()))
            .body(contentReportDTO);
    }

    /**
     * {@code PATCH  /content-reports/:id} : Partial updates given fields of an existing contentReport, field will ignore if it is null
     *
     * @param id the id of the contentReportDTO to save.
     * @param contentReportDTO the contentReportDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contentReportDTO,
     * or with status {@code 400 (Bad Request)} if the contentReportDTO is not valid,
     * or with status {@code 404 (Not Found)} if the contentReportDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the contentReportDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ContentReportDTO> partialUpdateContentReport(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ContentReportDTO contentReportDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update ContentReport : {}, {}", id, contentReportDTO);
        if (contentReportDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, contentReportDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!contentReportRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ContentReportDTO> result = contentReportService.partialUpdate(contentReportDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contentReportDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /content-reports} : get all the Content Reports.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Content Reports in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ContentReportDTO>> getAllContentReports(
        ContentReportCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ContentReports by criteria: {}", criteria);

        Page<ContentReportDTO> page = contentReportQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /content-reports/count} : count all the contentReports.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countContentReports(ContentReportCriteria criteria) {
        LOG.debug("REST request to count ContentReports by criteria: {}", criteria);
        return ResponseEntity.ok().body(contentReportQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /content-reports/:id} : get the "id" contentReport.
     *
     * @param id the id of the contentReportDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the contentReportDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContentReportDTO> getContentReport(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ContentReport : {}", id);
        Optional<ContentReportDTO> contentReportDTO = contentReportService.findOne(id);
        return ResponseUtil.wrapOrNotFound(contentReportDTO);
    }

    /**
     * {@code DELETE  /content-reports/:id} : delete the "id" contentReport.
     *
     * @param id the id of the contentReportDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContentReport(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ContentReport : {}", id);
        contentReportService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
