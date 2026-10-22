package com.lumi.web.rest;

import com.lumi.repository.SupportTicketRepository;
import com.lumi.service.SupportTicketQueryService;
import com.lumi.service.SupportTicketService;
import com.lumi.service.criteria.SupportTicketCriteria;
import com.lumi.service.dto.SupportTicketDTO;
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
 * REST controller for managing {@link com.lumi.domain.SupportTicket}.
 */
@RestController
@RequestMapping("/api/support-tickets")
public class SupportTicketResource {

    private static final Logger LOG = LoggerFactory.getLogger(SupportTicketResource.class);

    private static final String ENTITY_NAME = "supportTicket";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final SupportTicketService supportTicketService;

    private final SupportTicketRepository supportTicketRepository;

    private final SupportTicketQueryService supportTicketQueryService;

    public SupportTicketResource(
        SupportTicketService supportTicketService,
        SupportTicketRepository supportTicketRepository,
        SupportTicketQueryService supportTicketQueryService
    ) {
        this.supportTicketService = supportTicketService;
        this.supportTicketRepository = supportTicketRepository;
        this.supportTicketQueryService = supportTicketQueryService;
    }

    /**
     * {@code POST  /support-tickets} : Create a new supportTicket.
     *
     * @param supportTicketDTO the supportTicketDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new supportTicketDTO, or with status {@code 400 (Bad Request)} if the supportTicket already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SupportTicketDTO> createSupportTicket(@Valid @RequestBody SupportTicketDTO supportTicketDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save SupportTicket : {}", supportTicketDTO);
        if (supportTicketDTO.getId() != null) {
            throw new BadRequestAlertException("A new supportTicket cannot already have an ID", ENTITY_NAME, "idexists");
        }
        supportTicketDTO = supportTicketService.save(supportTicketDTO);
        return ResponseEntity.created(new URI("/api/support-tickets/" + supportTicketDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, supportTicketDTO.getId().toString()))
            .body(supportTicketDTO);
    }

    /**
     * {@code PUT  /support-tickets/:id} : Updates an existing supportTicket.
     *
     * @param id the id of the supportTicketDTO to save.
     * @param supportTicketDTO the supportTicketDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated supportTicketDTO,
     * or with status {@code 400 (Bad Request)} if the supportTicketDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the supportTicketDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SupportTicketDTO> updateSupportTicket(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SupportTicketDTO supportTicketDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SupportTicket : {}, {}", id, supportTicketDTO);
        if (supportTicketDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, supportTicketDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!supportTicketRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        supportTicketDTO = supportTicketService.update(supportTicketDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, supportTicketDTO.getId().toString()))
            .body(supportTicketDTO);
    }

    /**
     * {@code PATCH  /support-tickets/:id} : Partial updates given fields of an existing supportTicket, field will ignore if it is null
     *
     * @param id the id of the supportTicketDTO to save.
     * @param supportTicketDTO the supportTicketDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated supportTicketDTO,
     * or with status {@code 400 (Bad Request)} if the supportTicketDTO is not valid,
     * or with status {@code 404 (Not Found)} if the supportTicketDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the supportTicketDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SupportTicketDTO> partialUpdateSupportTicket(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SupportTicketDTO supportTicketDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update SupportTicket : {}, {}", id, supportTicketDTO);
        if (supportTicketDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, supportTicketDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!supportTicketRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SupportTicketDTO> result = supportTicketService.partialUpdate(supportTicketDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, supportTicketDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /support-tickets} : get all the Support Tickets.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Support Tickets in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SupportTicketDTO>> getAllSupportTickets(
        SupportTicketCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get SupportTickets by criteria: {}", criteria);

        Page<SupportTicketDTO> page = supportTicketQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /support-tickets/count} : count all the supportTickets.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSupportTickets(SupportTicketCriteria criteria) {
        LOG.debug("REST request to count SupportTickets by criteria: {}", criteria);
        return ResponseEntity.ok().body(supportTicketQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /support-tickets/:id} : get the "id" supportTicket.
     *
     * @param id the id of the supportTicketDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the supportTicketDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SupportTicketDTO> getSupportTicket(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SupportTicket : {}", id);
        Optional<SupportTicketDTO> supportTicketDTO = supportTicketService.findOne(id);
        return ResponseUtil.wrapOrNotFound(supportTicketDTO);
    }

    /**
     * {@code DELETE  /support-tickets/:id} : delete the "id" supportTicket.
     *
     * @param id the id of the supportTicketDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupportTicket(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SupportTicket : {}", id);
        supportTicketService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
