package com.lumi.web.rest;

import com.lumi.repository.TeacherQuotaRepository;
import com.lumi.service.TeacherQuotaQueryService;
import com.lumi.service.TeacherQuotaService;
import com.lumi.service.criteria.TeacherQuotaCriteria;
import com.lumi.service.dto.TeacherQuotaDTO;
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
 * REST controller for managing {@link com.lumi.domain.TeacherQuota}.
 */
@RestController
@RequestMapping("/api/teacher-quotas")
public class TeacherQuotaResource {

    private static final Logger LOG = LoggerFactory.getLogger(TeacherQuotaResource.class);

    private static final String ENTITY_NAME = "teacherQuota";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final TeacherQuotaService teacherQuotaService;

    private final TeacherQuotaRepository teacherQuotaRepository;

    private final TeacherQuotaQueryService teacherQuotaQueryService;

    public TeacherQuotaResource(
        TeacherQuotaService teacherQuotaService,
        TeacherQuotaRepository teacherQuotaRepository,
        TeacherQuotaQueryService teacherQuotaQueryService
    ) {
        this.teacherQuotaService = teacherQuotaService;
        this.teacherQuotaRepository = teacherQuotaRepository;
        this.teacherQuotaQueryService = teacherQuotaQueryService;
    }

    /**
     * {@code POST  /teacher-quotas} : Create a new teacherQuota.
     *
     * @param teacherQuotaDTO the teacherQuotaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new teacherQuotaDTO, or with status {@code 400 (Bad Request)} if the teacherQuota already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TeacherQuotaDTO> createTeacherQuota(@Valid @RequestBody TeacherQuotaDTO teacherQuotaDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TeacherQuota : {}", teacherQuotaDTO);
        if (teacherQuotaDTO.getId() != null) {
            throw new BadRequestAlertException("A new teacherQuota cannot already have an ID", ENTITY_NAME, "idexists");
        }
        teacherQuotaDTO = teacherQuotaService.save(teacherQuotaDTO);
        return ResponseEntity.created(new URI("/api/teacher-quotas/" + teacherQuotaDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, teacherQuotaDTO.getId().toString()))
            .body(teacherQuotaDTO);
    }

    /**
     * {@code PUT  /teacher-quotas/:id} : Updates an existing teacherQuota.
     *
     * @param id the id of the teacherQuotaDTO to save.
     * @param teacherQuotaDTO the teacherQuotaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teacherQuotaDTO,
     * or with status {@code 400 (Bad Request)} if the teacherQuotaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the teacherQuotaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TeacherQuotaDTO> updateTeacherQuota(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TeacherQuotaDTO teacherQuotaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TeacherQuota : {}, {}", id, teacherQuotaDTO);
        if (teacherQuotaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teacherQuotaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!teacherQuotaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        teacherQuotaDTO = teacherQuotaService.update(teacherQuotaDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, teacherQuotaDTO.getId().toString()))
            .body(teacherQuotaDTO);
    }

    /**
     * {@code PATCH  /teacher-quotas/:id} : Partial updates given fields of an existing teacherQuota, field will ignore if it is null
     *
     * @param id the id of the teacherQuotaDTO to save.
     * @param teacherQuotaDTO the teacherQuotaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teacherQuotaDTO,
     * or with status {@code 400 (Bad Request)} if the teacherQuotaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the teacherQuotaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the teacherQuotaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TeacherQuotaDTO> partialUpdateTeacherQuota(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TeacherQuotaDTO teacherQuotaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update TeacherQuota : {}, {}", id, teacherQuotaDTO);
        if (teacherQuotaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teacherQuotaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!teacherQuotaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TeacherQuotaDTO> result = teacherQuotaService.partialUpdate(teacherQuotaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, teacherQuotaDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /teacher-quotas} : get all the Teacher Quotas.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Teacher Quotas in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TeacherQuotaDTO>> getAllTeacherQuotas(TeacherQuotaCriteria criteria) {
        LOG.debug("REST request to get TeacherQuotas by criteria: {}", criteria);

        List<TeacherQuotaDTO> entityList = teacherQuotaQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /teacher-quotas/count} : count all the teacherQuotas.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTeacherQuotas(TeacherQuotaCriteria criteria) {
        LOG.debug("REST request to count TeacherQuotas by criteria: {}", criteria);
        return ResponseEntity.ok().body(teacherQuotaQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /teacher-quotas/:id} : get the "id" teacherQuota.
     *
     * @param id the id of the teacherQuotaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teacherQuotaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeacherQuotaDTO> getTeacherQuota(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TeacherQuota : {}", id);
        Optional<TeacherQuotaDTO> teacherQuotaDTO = teacherQuotaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(teacherQuotaDTO);
    }

    /**
     * {@code DELETE  /teacher-quotas/:id} : delete the "id" teacherQuota.
     *
     * @param id the id of the teacherQuotaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacherQuota(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TeacherQuota : {}", id);
        teacherQuotaService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
