package com.lumi.web.rest;

import com.lumi.repository.InstructorWalletRepository;
import com.lumi.service.InstructorWalletService;
import com.lumi.service.criteria.InstructorWalletCriteria;
import com.lumi.service.dto.InstructorWalletDTO;
import com.lumi.service.query.InstructorWalletQueryService;
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
 * REST controller for managing {@link com.lumi.domain.InstructorWallet}.
 */
@RestController
@RequestMapping("/api/instructor-wallets")
public class InstructorWalletResource {

    private static final Logger LOG = LoggerFactory.getLogger(InstructorWalletResource.class);

    private static final String ENTITY_NAME = "instructorWallet";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final InstructorWalletService instructorWalletService;

    private final InstructorWalletRepository instructorWalletRepository;

    private final InstructorWalletQueryService instructorWalletQueryService;

    public InstructorWalletResource(
        InstructorWalletService instructorWalletService,
        InstructorWalletRepository instructorWalletRepository,
        InstructorWalletQueryService instructorWalletQueryService
    ) {
        this.instructorWalletService = instructorWalletService;
        this.instructorWalletRepository = instructorWalletRepository;
        this.instructorWalletQueryService = instructorWalletQueryService;
    }

    /**
     * {@code POST  /instructor-wallets} : Create a new instructorWallet.
     *
     * @param instructorWalletDTO the instructorWalletDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new instructorWalletDTO, or with status {@code 400 (Bad Request)} if the instructorWallet already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InstructorWalletDTO> createInstructorWallet(@Valid @RequestBody InstructorWalletDTO instructorWalletDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save InstructorWallet : {}", instructorWalletDTO);
        if (instructorWalletDTO.getId() != null) {
            throw new BadRequestAlertException("A new instructorWallet cannot already have an ID", ENTITY_NAME, "idexists");
        }
        instructorWalletDTO = instructorWalletService.save(instructorWalletDTO);
        return ResponseEntity.created(new URI("/api/instructor-wallets/" + instructorWalletDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, instructorWalletDTO.getId().toString()))
            .body(instructorWalletDTO);
    }

    /**
     * {@code PUT  /instructor-wallets/:id} : Updates an existing instructorWallet.
     *
     * @param id the id of the instructorWalletDTO to save.
     * @param instructorWalletDTO the instructorWalletDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated instructorWalletDTO,
     * or with status {@code 400 (Bad Request)} if the instructorWalletDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the instructorWalletDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InstructorWalletDTO> updateInstructorWallet(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InstructorWalletDTO instructorWalletDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update InstructorWallet : {}, {}", id, instructorWalletDTO);
        if (instructorWalletDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, instructorWalletDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!instructorWalletRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        instructorWalletDTO = instructorWalletService.update(instructorWalletDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, instructorWalletDTO.getId().toString()))
            .body(instructorWalletDTO);
    }

    /**
     * {@code PATCH  /instructor-wallets/:id} : Partial updates given fields of an existing instructorWallet, field will ignore if it is null
     *
     * @param id the id of the instructorWalletDTO to save.
     * @param instructorWalletDTO the instructorWalletDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated instructorWalletDTO,
     * or with status {@code 400 (Bad Request)} if the instructorWalletDTO is not valid,
     * or with status {@code 404 (Not Found)} if the instructorWalletDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the instructorWalletDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InstructorWalletDTO> partialUpdateInstructorWallet(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InstructorWalletDTO instructorWalletDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update InstructorWallet : {}, {}", id, instructorWalletDTO);
        if (instructorWalletDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, instructorWalletDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!instructorWalletRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InstructorWalletDTO> result = instructorWalletService.partialUpdate(instructorWalletDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, instructorWalletDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /instructor-wallets} : get all the Instructor Wallets.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Instructor Wallets in body.
     */
    @GetMapping("")
    public ResponseEntity<List<InstructorWalletDTO>> getAllInstructorWallets(
        InstructorWalletCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get InstructorWallets by criteria: {}", criteria);

        Page<InstructorWalletDTO> page = instructorWalletQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /instructor-wallets/count} : count all the instructorWallets.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countInstructorWallets(InstructorWalletCriteria criteria) {
        LOG.debug("REST request to count InstructorWallets by criteria: {}", criteria);
        return ResponseEntity.ok().body(instructorWalletQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /instructor-wallets/:id} : get the "id" instructorWallet.
     *
     * @param id the id of the instructorWalletDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the instructorWalletDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InstructorWalletDTO> getInstructorWallet(@PathVariable("id") Long id) {
        LOG.debug("REST request to get InstructorWallet : {}", id);
        Optional<InstructorWalletDTO> instructorWalletDTO = instructorWalletService.findOne(id);
        return ResponseUtil.wrapOrNotFound(instructorWalletDTO);
    }

    /**
     * {@code DELETE  /instructor-wallets/:id} : delete the "id" instructorWallet.
     *
     * @param id the id of the instructorWalletDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstructorWallet(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete InstructorWallet : {}", id);
        instructorWalletService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
