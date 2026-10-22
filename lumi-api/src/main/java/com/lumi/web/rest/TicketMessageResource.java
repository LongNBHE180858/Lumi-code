package com.lumi.web.rest;

import com.lumi.repository.TicketMessageRepository;
import com.lumi.service.TicketMessageQueryService;
import com.lumi.service.TicketMessageService;
import com.lumi.service.criteria.TicketMessageCriteria;
import com.lumi.service.dto.TicketMessageDTO;
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
 * REST controller for managing {@link com.lumi.domain.TicketMessage}.
 */
@RestController
@RequestMapping("/api/ticket-messages")
public class TicketMessageResource {

    private static final Logger LOG = LoggerFactory.getLogger(TicketMessageResource.class);

    private static final String ENTITY_NAME = "ticketMessage";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final TicketMessageService ticketMessageService;

    private final TicketMessageRepository ticketMessageRepository;

    private final TicketMessageQueryService ticketMessageQueryService;

    public TicketMessageResource(
        TicketMessageService ticketMessageService,
        TicketMessageRepository ticketMessageRepository,
        TicketMessageQueryService ticketMessageQueryService
    ) {
        this.ticketMessageService = ticketMessageService;
        this.ticketMessageRepository = ticketMessageRepository;
        this.ticketMessageQueryService = ticketMessageQueryService;
    }

    /**
     * {@code POST  /ticket-messages} : Create a new ticketMessage.
     *
     * @param ticketMessageDTO the ticketMessageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ticketMessageDTO, or with status {@code 400 (Bad Request)} if the ticketMessage already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TicketMessageDTO> createTicketMessage(@Valid @RequestBody TicketMessageDTO ticketMessageDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TicketMessage : {}", ticketMessageDTO);
        if (ticketMessageDTO.getId() != null) {
            throw new BadRequestAlertException("A new ticketMessage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ticketMessageDTO = ticketMessageService.save(ticketMessageDTO);
        return ResponseEntity.created(new URI("/api/ticket-messages/" + ticketMessageDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, ticketMessageDTO.getId().toString()))
            .body(ticketMessageDTO);
    }

    /**
     * {@code PUT  /ticket-messages/:id} : Updates an existing ticketMessage.
     *
     * @param id the id of the ticketMessageDTO to save.
     * @param ticketMessageDTO the ticketMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketMessageDTO,
     * or with status {@code 400 (Bad Request)} if the ticketMessageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ticketMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TicketMessageDTO> updateTicketMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TicketMessageDTO ticketMessageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TicketMessage : {}, {}", id, ticketMessageDTO);
        if (ticketMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ticketMessageDTO = ticketMessageService.update(ticketMessageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ticketMessageDTO.getId().toString()))
            .body(ticketMessageDTO);
    }

    /**
     * {@code PATCH  /ticket-messages/:id} : Partial updates given fields of an existing ticketMessage, field will ignore if it is null
     *
     * @param id the id of the ticketMessageDTO to save.
     * @param ticketMessageDTO the ticketMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketMessageDTO,
     * or with status {@code 400 (Bad Request)} if the ticketMessageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ticketMessageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ticketMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TicketMessageDTO> partialUpdateTicketMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TicketMessageDTO ticketMessageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update TicketMessage : {}, {}", id, ticketMessageDTO);
        if (ticketMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TicketMessageDTO> result = ticketMessageService.partialUpdate(ticketMessageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ticketMessageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ticket-messages} : get all the Ticket Messages.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Ticket Messages in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TicketMessageDTO>> getAllTicketMessages(
        TicketMessageCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TicketMessages by criteria: {}", criteria);

        Page<TicketMessageDTO> page = ticketMessageQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ticket-messages/count} : count all the ticketMessages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTicketMessages(TicketMessageCriteria criteria) {
        LOG.debug("REST request to count TicketMessages by criteria: {}", criteria);
        return ResponseEntity.ok().body(ticketMessageQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ticket-messages/:id} : get the "id" ticketMessage.
     *
     * @param id the id of the ticketMessageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ticketMessageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketMessageDTO> getTicketMessage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TicketMessage : {}", id);
        Optional<TicketMessageDTO> ticketMessageDTO = ticketMessageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ticketMessageDTO);
    }

    /**
     * {@code DELETE  /ticket-messages/:id} : delete the "id" ticketMessage.
     *
     * @param id the id of the ticketMessageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketMessage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TicketMessage : {}", id);
        ticketMessageService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
