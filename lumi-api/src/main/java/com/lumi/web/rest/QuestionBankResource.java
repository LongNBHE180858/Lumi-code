package com.lumi.web.rest;

import com.lumi.repository.QuestionBankRepository;
import com.lumi.service.QuestionBankQueryService;
import com.lumi.service.QuestionBankService;
import com.lumi.service.criteria.QuestionBankCriteria;
import com.lumi.service.dto.QuestionBankDTO;
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
 * REST controller for managing {@link com.lumi.domain.QuestionBank}.
 */
@RestController
@RequestMapping("/api/question-banks")
public class QuestionBankResource {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionBankResource.class);

    private static final String ENTITY_NAME = "questionBank";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final QuestionBankService questionBankService;

    private final QuestionBankRepository questionBankRepository;

    private final QuestionBankQueryService questionBankQueryService;

    public QuestionBankResource(
        QuestionBankService questionBankService,
        QuestionBankRepository questionBankRepository,
        QuestionBankQueryService questionBankQueryService
    ) {
        this.questionBankService = questionBankService;
        this.questionBankRepository = questionBankRepository;
        this.questionBankQueryService = questionBankQueryService;
    }

    /**
     * {@code POST  /question-banks} : Create a new questionBank.
     *
     * @param questionBankDTO the questionBankDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new questionBankDTO, or with status {@code 400 (Bad Request)} if the questionBank already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<QuestionBankDTO> createQuestionBank(@Valid @RequestBody QuestionBankDTO questionBankDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save QuestionBank : {}", questionBankDTO);
        if (questionBankDTO.getId() != null) {
            throw new BadRequestAlertException("A new questionBank cannot already have an ID", ENTITY_NAME, "idexists");
        }
        questionBankDTO = questionBankService.save(questionBankDTO);
        return ResponseEntity.created(new URI("/api/question-banks/" + questionBankDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, questionBankDTO.getId().toString()))
            .body(questionBankDTO);
    }

    /**
     * {@code PUT  /question-banks/:id} : Updates an existing questionBank.
     *
     * @param id the id of the questionBankDTO to save.
     * @param questionBankDTO the questionBankDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated questionBankDTO,
     * or with status {@code 400 (Bad Request)} if the questionBankDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the questionBankDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<QuestionBankDTO> updateQuestionBank(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody QuestionBankDTO questionBankDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update QuestionBank : {}, {}", id, questionBankDTO);
        if (questionBankDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, questionBankDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!questionBankRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        questionBankDTO = questionBankService.update(questionBankDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, questionBankDTO.getId().toString()))
            .body(questionBankDTO);
    }

    /**
     * {@code PATCH  /question-banks/:id} : Partial updates given fields of an existing questionBank, field will ignore if it is null
     *
     * @param id the id of the questionBankDTO to save.
     * @param questionBankDTO the questionBankDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated questionBankDTO,
     * or with status {@code 400 (Bad Request)} if the questionBankDTO is not valid,
     * or with status {@code 404 (Not Found)} if the questionBankDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the questionBankDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<QuestionBankDTO> partialUpdateQuestionBank(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody QuestionBankDTO questionBankDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update QuestionBank : {}, {}", id, questionBankDTO);
        if (questionBankDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, questionBankDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!questionBankRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<QuestionBankDTO> result = questionBankService.partialUpdate(questionBankDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, questionBankDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /question-banks} : get all the Question Banks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Question Banks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<QuestionBankDTO>> getAllQuestionBanks(
        QuestionBankCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get QuestionBanks by criteria: {}", criteria);

        Page<QuestionBankDTO> page = questionBankQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /question-banks/count} : count all the questionBanks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countQuestionBanks(QuestionBankCriteria criteria) {
        LOG.debug("REST request to count QuestionBanks by criteria: {}", criteria);
        return ResponseEntity.ok().body(questionBankQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /question-banks/:id} : get the "id" questionBank.
     *
     * @param id the id of the questionBankDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the questionBankDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuestionBankDTO> getQuestionBank(@PathVariable("id") Long id) {
        LOG.debug("REST request to get QuestionBank : {}", id);
        Optional<QuestionBankDTO> questionBankDTO = questionBankService.findOne(id);
        return ResponseUtil.wrapOrNotFound(questionBankDTO);
    }

    /**
     * {@code DELETE  /question-banks/:id} : delete the "id" questionBank.
     *
     * @param id the id of the questionBankDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestionBank(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete QuestionBank : {}", id);
        questionBankService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
