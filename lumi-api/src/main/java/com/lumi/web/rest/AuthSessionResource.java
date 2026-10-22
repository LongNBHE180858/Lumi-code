package com.lumi.web.rest;

import com.lumi.repository.AuthSessionRepository;
import com.lumi.service.AuthSessionQueryService;
import com.lumi.service.AuthSessionService;
import com.lumi.service.criteria.AuthSessionCriteria;
import com.lumi.service.dto.AuthSessionDTO;
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
 * REST controller for managing {@link com.lumi.domain.AuthSession}.
 */
@RestController
@RequestMapping("/api/auth-sessions")
public class AuthSessionResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuthSessionResource.class);

    private static final String ENTITY_NAME = "authSession";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final AuthSessionService authSessionService;

    private final AuthSessionRepository authSessionRepository;

    private final AuthSessionQueryService authSessionQueryService;

    public AuthSessionResource(
        AuthSessionService authSessionService,
        AuthSessionRepository authSessionRepository,
        AuthSessionQueryService authSessionQueryService
    ) {
        this.authSessionService = authSessionService;
        this.authSessionRepository = authSessionRepository;
        this.authSessionQueryService = authSessionQueryService;
    }

    /**
     * {@code POST  /auth-sessions} : Create a new authSession.
     *
     * @param authSessionDTO the authSessionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new authSessionDTO, or with status {@code 400 (Bad Request)} if the authSession already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AuthSessionDTO> createAuthSession(@Valid @RequestBody AuthSessionDTO authSessionDTO) throws URISyntaxException {
        LOG.debug("REST request to save AuthSession : {}", authSessionDTO);
        if (authSessionDTO.getId() != null) {
            throw new BadRequestAlertException("A new authSession cannot already have an ID", ENTITY_NAME, "idexists");
        }
        authSessionDTO = authSessionService.save(authSessionDTO);
        return ResponseEntity.created(new URI("/api/auth-sessions/" + authSessionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, authSessionDTO.getId().toString()))
            .body(authSessionDTO);
    }

    /**
     * {@code PUT  /auth-sessions/:id} : Updates an existing authSession.
     *
     * @param id the id of the authSessionDTO to save.
     * @param authSessionDTO the authSessionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated authSessionDTO,
     * or with status {@code 400 (Bad Request)} if the authSessionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the authSessionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AuthSessionDTO> updateAuthSession(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AuthSessionDTO authSessionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AuthSession : {}, {}", id, authSessionDTO);
        if (authSessionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, authSessionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!authSessionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        authSessionDTO = authSessionService.update(authSessionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, authSessionDTO.getId().toString()))
            .body(authSessionDTO);
    }

    /**
     * {@code PATCH  /auth-sessions/:id} : Partial updates given fields of an existing authSession, field will ignore if it is null
     *
     * @param id the id of the authSessionDTO to save.
     * @param authSessionDTO the authSessionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated authSessionDTO,
     * or with status {@code 400 (Bad Request)} if the authSessionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the authSessionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the authSessionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AuthSessionDTO> partialUpdateAuthSession(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AuthSessionDTO authSessionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update AuthSession : {}, {}", id, authSessionDTO);
        if (authSessionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, authSessionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!authSessionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AuthSessionDTO> result = authSessionService.partialUpdate(authSessionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, authSessionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /auth-sessions} : get all the Auth Sessions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Auth Sessions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AuthSessionDTO>> getAllAuthSessions(
        AuthSessionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get AuthSessions by criteria: {}", criteria);

        Page<AuthSessionDTO> page = authSessionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /auth-sessions/count} : count all the authSessions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAuthSessions(AuthSessionCriteria criteria) {
        LOG.debug("REST request to count AuthSessions by criteria: {}", criteria);
        return ResponseEntity.ok().body(authSessionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /auth-sessions/:id} : get the "id" authSession.
     *
     * @param id the id of the authSessionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the authSessionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuthSessionDTO> getAuthSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AuthSession : {}", id);
        Optional<AuthSessionDTO> authSessionDTO = authSessionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(authSessionDTO);
    }

    /**
     * {@code DELETE  /auth-sessions/:id} : delete the "id" authSession.
     *
     * @param id the id of the authSessionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AuthSession : {}", id);
        authSessionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
