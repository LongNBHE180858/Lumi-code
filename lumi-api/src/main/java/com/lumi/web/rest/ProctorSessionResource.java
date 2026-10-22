package com.lumi.web.rest;

import com.lumi.repository.ProctorSessionRepository;
import com.lumi.service.ProctorSessionQueryService;
import com.lumi.service.ProctorSessionService;
import com.lumi.service.criteria.ProctorSessionCriteria;
import com.lumi.service.dto.ProctorSessionDTO;
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
 * REST controller for managing {@link com.lumi.domain.ProctorSession}.
 */
@RestController
@RequestMapping("/api/proctor-sessions")
public class ProctorSessionResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProctorSessionResource.class);

    private static final String ENTITY_NAME = "proctorSession";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final ProctorSessionService proctorSessionService;

    private final ProctorSessionRepository proctorSessionRepository;

    private final ProctorSessionQueryService proctorSessionQueryService;

    public ProctorSessionResource(
        ProctorSessionService proctorSessionService,
        ProctorSessionRepository proctorSessionRepository,
        ProctorSessionQueryService proctorSessionQueryService
    ) {
        this.proctorSessionService = proctorSessionService;
        this.proctorSessionRepository = proctorSessionRepository;
        this.proctorSessionQueryService = proctorSessionQueryService;
    }

    /**
     * {@code POST  /proctor-sessions} : Create a new proctorSession.
     *
     * @param proctorSessionDTO the proctorSessionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new proctorSessionDTO, or with status {@code 400 (Bad Request)} if the proctorSession already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProctorSessionDTO> createProctorSession(@Valid @RequestBody ProctorSessionDTO proctorSessionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ProctorSession : {}", proctorSessionDTO);
        if (proctorSessionDTO.getId() != null) {
            throw new BadRequestAlertException("A new proctorSession cannot already have an ID", ENTITY_NAME, "idexists");
        }
        proctorSessionDTO = proctorSessionService.save(proctorSessionDTO);
        return ResponseEntity.created(new URI("/api/proctor-sessions/" + proctorSessionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, proctorSessionDTO.getId().toString()))
            .body(proctorSessionDTO);
    }

    /**
     * {@code PUT  /proctor-sessions/:id} : Updates an existing proctorSession.
     *
     * @param id the id of the proctorSessionDTO to save.
     * @param proctorSessionDTO the proctorSessionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated proctorSessionDTO,
     * or with status {@code 400 (Bad Request)} if the proctorSessionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the proctorSessionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProctorSessionDTO> updateProctorSession(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProctorSessionDTO proctorSessionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProctorSession : {}, {}", id, proctorSessionDTO);
        if (proctorSessionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, proctorSessionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!proctorSessionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        proctorSessionDTO = proctorSessionService.update(proctorSessionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, proctorSessionDTO.getId().toString()))
            .body(proctorSessionDTO);
    }

    /**
     * {@code PATCH  /proctor-sessions/:id} : Partial updates given fields of an existing proctorSession, field will ignore if it is null
     *
     * @param id the id of the proctorSessionDTO to save.
     * @param proctorSessionDTO the proctorSessionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated proctorSessionDTO,
     * or with status {@code 400 (Bad Request)} if the proctorSessionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the proctorSessionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the proctorSessionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProctorSessionDTO> partialUpdateProctorSession(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProctorSessionDTO proctorSessionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update ProctorSession : {}, {}", id, proctorSessionDTO);
        if (proctorSessionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, proctorSessionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!proctorSessionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProctorSessionDTO> result = proctorSessionService.partialUpdate(proctorSessionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, proctorSessionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /proctor-sessions} : get all the Proctor Sessions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Proctor Sessions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ProctorSessionDTO>> getAllProctorSessions(ProctorSessionCriteria criteria) {
        LOG.debug("REST request to get ProctorSessions by criteria: {}", criteria);

        List<ProctorSessionDTO> entityList = proctorSessionQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /proctor-sessions/count} : count all the proctorSessions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countProctorSessions(ProctorSessionCriteria criteria) {
        LOG.debug("REST request to count ProctorSessions by criteria: {}", criteria);
        return ResponseEntity.ok().body(proctorSessionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /proctor-sessions/:id} : get the "id" proctorSession.
     *
     * @param id the id of the proctorSessionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the proctorSessionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProctorSessionDTO> getProctorSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProctorSession : {}", id);
        Optional<ProctorSessionDTO> proctorSessionDTO = proctorSessionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(proctorSessionDTO);
    }

    /**
     * {@code DELETE  /proctor-sessions/:id} : delete the "id" proctorSession.
     *
     * @param id the id of the proctorSessionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProctorSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProctorSession : {}", id);
        proctorSessionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
