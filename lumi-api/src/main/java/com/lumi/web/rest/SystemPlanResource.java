package com.lumi.web.rest;

import com.lumi.repository.SystemPlanRepository;
import com.lumi.service.SystemPlanQueryService;
import com.lumi.service.SystemPlanService;
import com.lumi.service.criteria.SystemPlanCriteria;
import com.lumi.service.dto.SystemPlanDTO;
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
 * REST controller for managing {@link com.lumi.domain.SystemPlan}.
 */
@RestController
@RequestMapping("/api/system-plans")
public class SystemPlanResource {

    private static final Logger LOG = LoggerFactory.getLogger(SystemPlanResource.class);

    private static final String ENTITY_NAME = "systemPlan";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final SystemPlanService systemPlanService;

    private final SystemPlanRepository systemPlanRepository;

    private final SystemPlanQueryService systemPlanQueryService;

    public SystemPlanResource(
        SystemPlanService systemPlanService,
        SystemPlanRepository systemPlanRepository,
        SystemPlanQueryService systemPlanQueryService
    ) {
        this.systemPlanService = systemPlanService;
        this.systemPlanRepository = systemPlanRepository;
        this.systemPlanQueryService = systemPlanQueryService;
    }

    /**
     * {@code POST  /system-plans} : Create a new systemPlan.
     *
     * @param systemPlanDTO the systemPlanDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new systemPlanDTO, or with status {@code 400 (Bad Request)} if the systemPlan already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SystemPlanDTO> createSystemPlan(@Valid @RequestBody SystemPlanDTO systemPlanDTO) throws URISyntaxException {
        LOG.debug("REST request to save SystemPlan : {}", systemPlanDTO);
        if (systemPlanDTO.getId() != null) {
            throw new BadRequestAlertException("A new systemPlan cannot already have an ID", ENTITY_NAME, "idexists");
        }
        systemPlanDTO = systemPlanService.save(systemPlanDTO);
        return ResponseEntity.created(new URI("/api/system-plans/" + systemPlanDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, systemPlanDTO.getId().toString()))
            .body(systemPlanDTO);
    }

    /**
     * {@code PUT  /system-plans/:id} : Updates an existing systemPlan.
     *
     * @param id the id of the systemPlanDTO to save.
     * @param systemPlanDTO the systemPlanDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated systemPlanDTO,
     * or with status {@code 400 (Bad Request)} if the systemPlanDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the systemPlanDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SystemPlanDTO> updateSystemPlan(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SystemPlanDTO systemPlanDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SystemPlan : {}, {}", id, systemPlanDTO);
        if (systemPlanDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, systemPlanDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!systemPlanRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        systemPlanDTO = systemPlanService.update(systemPlanDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, systemPlanDTO.getId().toString()))
            .body(systemPlanDTO);
    }

    /**
     * {@code PATCH  /system-plans/:id} : Partial updates given fields of an existing systemPlan, field will ignore if it is null
     *
     * @param id the id of the systemPlanDTO to save.
     * @param systemPlanDTO the systemPlanDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated systemPlanDTO,
     * or with status {@code 400 (Bad Request)} if the systemPlanDTO is not valid,
     * or with status {@code 404 (Not Found)} if the systemPlanDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the systemPlanDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SystemPlanDTO> partialUpdateSystemPlan(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SystemPlanDTO systemPlanDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update SystemPlan : {}, {}", id, systemPlanDTO);
        if (systemPlanDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, systemPlanDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!systemPlanRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SystemPlanDTO> result = systemPlanService.partialUpdate(systemPlanDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, systemPlanDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /system-plans} : get all the System Plans.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of System Plans in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SystemPlanDTO>> getAllSystemPlans(
        SystemPlanCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get SystemPlans by criteria: {}", criteria);

        Page<SystemPlanDTO> page = systemPlanQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /system-plans/count} : count all the systemPlans.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSystemPlans(SystemPlanCriteria criteria) {
        LOG.debug("REST request to count SystemPlans by criteria: {}", criteria);
        return ResponseEntity.ok().body(systemPlanQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /system-plans/:id} : get the "id" systemPlan.
     *
     * @param id the id of the systemPlanDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the systemPlanDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SystemPlanDTO> getSystemPlan(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SystemPlan : {}", id);
        Optional<SystemPlanDTO> systemPlanDTO = systemPlanService.findOne(id);
        return ResponseUtil.wrapOrNotFound(systemPlanDTO);
    }

    /**
     * {@code DELETE  /system-plans/:id} : delete the "id" systemPlan.
     *
     * @param id the id of the systemPlanDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSystemPlan(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SystemPlan : {}", id);
        systemPlanService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
