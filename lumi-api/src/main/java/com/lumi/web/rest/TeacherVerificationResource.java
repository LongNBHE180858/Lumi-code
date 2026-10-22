package com.lumi.web.rest;

import com.lumi.repository.TeacherVerificationRepository;
import com.lumi.service.TeacherVerificationQueryService;
import com.lumi.service.TeacherVerificationService;
import com.lumi.service.criteria.TeacherVerificationCriteria;
import com.lumi.service.dto.TeacherVerificationDTO;
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
 * REST controller for managing {@link com.lumi.domain.TeacherVerification}.
 */
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RestController
@RequestMapping("/api/teacher-verifications")
public class TeacherVerificationResource {

    private static final Logger LOG = LoggerFactory.getLogger(TeacherVerificationResource.class);

    private static final String ENTITY_NAME = "teacherVerification";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final TeacherVerificationService teacherVerificationService;

    private final TeacherVerificationRepository teacherVerificationRepository;

    private final TeacherVerificationQueryService teacherVerificationQueryService;

    public TeacherVerificationResource(
        TeacherVerificationService teacherVerificationService,
        TeacherVerificationRepository teacherVerificationRepository,
        TeacherVerificationQueryService teacherVerificationQueryService
    ) {
        this.teacherVerificationService = teacherVerificationService;
        this.teacherVerificationRepository = teacherVerificationRepository;
        this.teacherVerificationQueryService = teacherVerificationQueryService;
    }

    /**
     * {@code POST  /teacher-verifications} : Create a new teacherVerification.
     *
     * @param teacherVerificationDTO the teacherVerificationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new teacherVerificationDTO, or with status {@code 400 (Bad Request)} if the teacherVerification already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TeacherVerificationDTO> createTeacherVerification(
        @Valid @RequestBody TeacherVerificationDTO teacherVerificationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save TeacherVerification : {}", teacherVerificationDTO);
        if (teacherVerificationDTO.getId() != null) {
            throw new BadRequestAlertException("A new teacherVerification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        teacherVerificationDTO = teacherVerificationService.save(teacherVerificationDTO);
        return ResponseEntity.created(new URI("/api/teacher-verifications/" + teacherVerificationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, teacherVerificationDTO.getId().toString()))
            .body(teacherVerificationDTO);
    }

    /**
     * {@code PUT  /teacher-verifications/:id} : Updates an existing teacherVerification.
     *
     * @param id the id of the teacherVerificationDTO to save.
     * @param teacherVerificationDTO the teacherVerificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teacherVerificationDTO,
     * or with status {@code 400 (Bad Request)} if the teacherVerificationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the teacherVerificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TeacherVerificationDTO> updateTeacherVerification(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TeacherVerificationDTO teacherVerificationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TeacherVerification : {}, {}", id, teacherVerificationDTO);
        if (teacherVerificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teacherVerificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!teacherVerificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        teacherVerificationDTO = teacherVerificationService.update(teacherVerificationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, teacherVerificationDTO.getId().toString()))
            .body(teacherVerificationDTO);
    }

    /**
     * {@code PATCH  /teacher-verifications/:id} : Partial updates given fields of an existing teacherVerification, field will ignore if it is null
     *
     * @param id the id of the teacherVerificationDTO to save.
     * @param teacherVerificationDTO the teacherVerificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teacherVerificationDTO,
     * or with status {@code 400 (Bad Request)} if the teacherVerificationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the teacherVerificationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the teacherVerificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TeacherVerificationDTO> partialUpdateTeacherVerification(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TeacherVerificationDTO teacherVerificationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update TeacherVerification : {}, {}", id, teacherVerificationDTO);
        if (teacherVerificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teacherVerificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!teacherVerificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TeacherVerificationDTO> result = teacherVerificationService.partialUpdate(teacherVerificationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, teacherVerificationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /teacher-verifications} : get all the Teacher Verifications.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Teacher Verifications in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TeacherVerificationDTO>> getAllTeacherVerifications(
        TeacherVerificationCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TeacherVerifications by criteria: {}", criteria);

        Page<TeacherVerificationDTO> page = teacherVerificationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /teacher-verifications/count} : count all the teacherVerifications.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTeacherVerifications(TeacherVerificationCriteria criteria) {
        LOG.debug("REST request to count TeacherVerifications by criteria: {}", criteria);
        return ResponseEntity.ok().body(teacherVerificationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /teacher-verifications/:id} : get the "id" teacherVerification.
     *
     * @param id the id of the teacherVerificationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teacherVerificationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeacherVerificationDTO> getTeacherVerification(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TeacherVerification : {}", id);
        Optional<TeacherVerificationDTO> teacherVerificationDTO = teacherVerificationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(teacherVerificationDTO);
    }

    /**
     * {@code DELETE  /teacher-verifications/:id} : delete the "id" teacherVerification.
     *
     * @param id the id of the teacherVerificationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacherVerification(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TeacherVerification : {}", id);
        teacherVerificationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
