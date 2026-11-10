package com.lumi.web.rest;

import com.lumi.repository.PayoutRequestRepository;
import com.lumi.service.PayoutRequestService;
import com.lumi.service.criteria.PayoutRequestCriteria;
import com.lumi.service.dto.PayoutRequestDTO;
import com.lumi.service.query.PayoutRequestQueryService;
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
 * REST controller for managing {@link com.lumi.domain.PayoutRequest}.
 */
@RestController
@org.springframework.security.access.prepost.PreAuthorize("@instructorAccessService.canUseInstructorFeatures()")
@RequestMapping("/api/payout-requests")
public class PayoutRequestResource {

    private static final Logger LOG = LoggerFactory.getLogger(PayoutRequestResource.class);

    private static final String ENTITY_NAME = "payoutRequest";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final PayoutRequestService payoutRequestService;

    private final PayoutRequestRepository payoutRequestRepository;

    private final PayoutRequestQueryService payoutRequestQueryService;

    public PayoutRequestResource(
        PayoutRequestService payoutRequestService,
        PayoutRequestRepository payoutRequestRepository,
        PayoutRequestQueryService payoutRequestQueryService
    ) {
        this.payoutRequestService = payoutRequestService;
        this.payoutRequestRepository = payoutRequestRepository;
        this.payoutRequestQueryService = payoutRequestQueryService;
    }

    /**
     * {@code POST  /payout-requests} : Create a new payoutRequest.
     *
     * @param payoutRequestDTO the payoutRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new payoutRequestDTO, or with status {@code 400 (Bad Request)} if the payoutRequest already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PayoutRequestDTO> createPayoutRequest(@Valid @RequestBody PayoutRequestDTO payoutRequestDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PayoutRequest : {}", payoutRequestDTO);
        if (payoutRequestDTO.getId() != null) {
            throw new BadRequestAlertException("A new payoutRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        payoutRequestDTO = payoutRequestService.save(payoutRequestDTO);
        return ResponseEntity.created(new URI("/api/payout-requests/" + payoutRequestDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, payoutRequestDTO.getId().toString()))
            .body(payoutRequestDTO);
    }

    /**
     * {@code PUT  /payout-requests/:id} : Updates an existing payoutRequest.
     *
     * @param id the id of the payoutRequestDTO to save.
     * @param payoutRequestDTO the payoutRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated payoutRequestDTO,
     * or with status {@code 400 (Bad Request)} if the payoutRequestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the payoutRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PayoutRequestDTO> updatePayoutRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PayoutRequestDTO payoutRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PayoutRequest : {}, {}", id, payoutRequestDTO);
        if (payoutRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, payoutRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!payoutRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        payoutRequestDTO = payoutRequestService.update(payoutRequestDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, payoutRequestDTO.getId().toString()))
            .body(payoutRequestDTO);
    }

    /**
     * {@code PATCH  /payout-requests/:id} : Partial updates given fields of an existing payoutRequest, field will ignore if it is null
     *
     * @param id the id of the payoutRequestDTO to save.
     * @param payoutRequestDTO the payoutRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated payoutRequestDTO,
     * or with status {@code 400 (Bad Request)} if the payoutRequestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the payoutRequestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the payoutRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PayoutRequestDTO> partialUpdatePayoutRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PayoutRequestDTO payoutRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update PayoutRequest : {}, {}", id, payoutRequestDTO);
        if (payoutRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, payoutRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!payoutRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PayoutRequestDTO> result = payoutRequestService.partialUpdate(payoutRequestDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, payoutRequestDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /payout-requests} : get all the Payout Requests.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Payout Requests in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PayoutRequestDTO>> getAllPayoutRequests(
        PayoutRequestCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get PayoutRequests by criteria: {}", criteria);

        Page<PayoutRequestDTO> page = payoutRequestQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /payout-requests/count} : count all the payoutRequests.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPayoutRequests(PayoutRequestCriteria criteria) {
        LOG.debug("REST request to count PayoutRequests by criteria: {}", criteria);
        return ResponseEntity.ok().body(payoutRequestQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /payout-requests/:id} : get the "id" payoutRequest.
     *
     * @param id the id of the payoutRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the payoutRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PayoutRequestDTO> getPayoutRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PayoutRequest : {}", id);
        Optional<PayoutRequestDTO> payoutRequestDTO = payoutRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(payoutRequestDTO);
    }

    /**
     * {@code DELETE  /payout-requests/:id} : delete the "id" payoutRequest.
     *
     * @param id the id of the payoutRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayoutRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PayoutRequest : {}", id);
        payoutRequestService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
