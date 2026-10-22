package com.lumi.web.rest;

import com.lumi.repository.ProctorEventRepository;
import com.lumi.service.ProctorEventQueryService;
import com.lumi.service.ProctorEventService;
import com.lumi.service.criteria.ProctorEventCriteria;
import com.lumi.service.dto.ProctorEventDTO;
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
 * REST controller for managing {@link com.lumi.domain.ProctorEvent}.
 */
@RestController
@RequestMapping("/api/proctor-events")
public class ProctorEventResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProctorEventResource.class);

    private static final String ENTITY_NAME = "proctorEvent";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final ProctorEventService proctorEventService;

    private final ProctorEventRepository proctorEventRepository;

    private final ProctorEventQueryService proctorEventQueryService;

    public ProctorEventResource(
        ProctorEventService proctorEventService,
        ProctorEventRepository proctorEventRepository,
        ProctorEventQueryService proctorEventQueryService
    ) {
        this.proctorEventService = proctorEventService;
        this.proctorEventRepository = proctorEventRepository;
        this.proctorEventQueryService = proctorEventQueryService;
    }

    /**
     * {@code POST  /proctor-events} : Create a new proctorEvent.
     *
     * @param proctorEventDTO the proctorEventDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new proctorEventDTO, or with status {@code 400 (Bad Request)} if the proctorEvent already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProctorEventDTO> createProctorEvent(@Valid @RequestBody ProctorEventDTO proctorEventDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ProctorEvent : {}", proctorEventDTO);
        if (proctorEventDTO.getId() != null) {
            throw new BadRequestAlertException("A new proctorEvent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        proctorEventDTO = proctorEventService.save(proctorEventDTO);
        return ResponseEntity.created(new URI("/api/proctor-events/" + proctorEventDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, proctorEventDTO.getId().toString()))
            .body(proctorEventDTO);
    }

    /**
     * {@code PUT  /proctor-events/:id} : Updates an existing proctorEvent.
     *
     * @param id the id of the proctorEventDTO to save.
     * @param proctorEventDTO the proctorEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated proctorEventDTO,
     * or with status {@code 400 (Bad Request)} if the proctorEventDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the proctorEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProctorEventDTO> updateProctorEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProctorEventDTO proctorEventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProctorEvent : {}, {}", id, proctorEventDTO);
        if (proctorEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, proctorEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!proctorEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        proctorEventDTO = proctorEventService.update(proctorEventDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, proctorEventDTO.getId().toString()))
            .body(proctorEventDTO);
    }

    /**
     * {@code PATCH  /proctor-events/:id} : Partial updates given fields of an existing proctorEvent, field will ignore if it is null
     *
     * @param id the id of the proctorEventDTO to save.
     * @param proctorEventDTO the proctorEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated proctorEventDTO,
     * or with status {@code 400 (Bad Request)} if the proctorEventDTO is not valid,
     * or with status {@code 404 (Not Found)} if the proctorEventDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the proctorEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProctorEventDTO> partialUpdateProctorEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProctorEventDTO proctorEventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update ProctorEvent : {}, {}", id, proctorEventDTO);
        if (proctorEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, proctorEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!proctorEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProctorEventDTO> result = proctorEventService.partialUpdate(proctorEventDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, proctorEventDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /proctor-events} : get all the Proctor Events.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Proctor Events in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ProctorEventDTO>> getAllProctorEvents(
        ProctorEventCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ProctorEvents by criteria: {}", criteria);

        Page<ProctorEventDTO> page = proctorEventQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /proctor-events/count} : count all the proctorEvents.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countProctorEvents(ProctorEventCriteria criteria) {
        LOG.debug("REST request to count ProctorEvents by criteria: {}", criteria);
        return ResponseEntity.ok().body(proctorEventQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /proctor-events/:id} : get the "id" proctorEvent.
     *
     * @param id the id of the proctorEventDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the proctorEventDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProctorEventDTO> getProctorEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProctorEvent : {}", id);
        Optional<ProctorEventDTO> proctorEventDTO = proctorEventService.findOne(id);
        return ResponseUtil.wrapOrNotFound(proctorEventDTO);
    }

    /**
     * {@code DELETE  /proctor-events/:id} : delete the "id" proctorEvent.
     *
     * @param id the id of the proctorEventDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProctorEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProctorEvent : {}", id);
        proctorEventService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
