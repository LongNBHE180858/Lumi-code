package com.lumi.web.rest;

import com.lumi.repository.QuestionKnowledgeLinkRepository;
import com.lumi.service.QuestionKnowledgeLinkService;
import com.lumi.service.criteria.QuestionKnowledgeLinkCriteria;
import com.lumi.service.dto.QuestionKnowledgeLinkDTO;
import com.lumi.service.query.QuestionKnowledgeLinkQueryService;
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
 * REST controller for managing {@link com.lumi.domain.QuestionKnowledgeLink}.
 */
@RestController
@RequestMapping("/api/question-knowledge-links")
public class QuestionKnowledgeLinkResource {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionKnowledgeLinkResource.class);

    private static final String ENTITY_NAME = "questionKnowledgeLink";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final QuestionKnowledgeLinkService questionKnowledgeLinkService;

    private final QuestionKnowledgeLinkRepository questionKnowledgeLinkRepository;

    private final QuestionKnowledgeLinkQueryService questionKnowledgeLinkQueryService;

    public QuestionKnowledgeLinkResource(
        QuestionKnowledgeLinkService questionKnowledgeLinkService,
        QuestionKnowledgeLinkRepository questionKnowledgeLinkRepository,
        QuestionKnowledgeLinkQueryService questionKnowledgeLinkQueryService
    ) {
        this.questionKnowledgeLinkService = questionKnowledgeLinkService;
        this.questionKnowledgeLinkRepository = questionKnowledgeLinkRepository;
        this.questionKnowledgeLinkQueryService = questionKnowledgeLinkQueryService;
    }

    /**
     * {@code POST  /question-knowledge-links} : Create a new questionKnowledgeLink.
     *
     * @param questionKnowledgeLinkDTO the questionKnowledgeLinkDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new questionKnowledgeLinkDTO, or with status {@code 400 (Bad Request)} if the questionKnowledgeLink already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<QuestionKnowledgeLinkDTO> createQuestionKnowledgeLink(
        @Valid @RequestBody QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save QuestionKnowledgeLink : {}", questionKnowledgeLinkDTO);
        if (questionKnowledgeLinkDTO.getId() != null) {
            throw new BadRequestAlertException("A new questionKnowledgeLink cannot already have an ID", ENTITY_NAME, "idexists");
        }
        questionKnowledgeLinkDTO = questionKnowledgeLinkService.save(questionKnowledgeLinkDTO);
        return ResponseEntity.created(new URI("/api/question-knowledge-links/" + questionKnowledgeLinkDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, questionKnowledgeLinkDTO.getId().toString()))
            .body(questionKnowledgeLinkDTO);
    }

    /**
     * {@code PUT  /question-knowledge-links/:id} : Updates an existing questionKnowledgeLink.
     *
     * @param id the id of the questionKnowledgeLinkDTO to save.
     * @param questionKnowledgeLinkDTO the questionKnowledgeLinkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated questionKnowledgeLinkDTO,
     * or with status {@code 400 (Bad Request)} if the questionKnowledgeLinkDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the questionKnowledgeLinkDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<QuestionKnowledgeLinkDTO> updateQuestionKnowledgeLink(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update QuestionKnowledgeLink : {}, {}", id, questionKnowledgeLinkDTO);
        if (questionKnowledgeLinkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, questionKnowledgeLinkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!questionKnowledgeLinkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        questionKnowledgeLinkDTO = questionKnowledgeLinkService.update(questionKnowledgeLinkDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, questionKnowledgeLinkDTO.getId().toString()))
            .body(questionKnowledgeLinkDTO);
    }

    /**
     * {@code PATCH  /question-knowledge-links/:id} : Partial updates given fields of an existing questionKnowledgeLink, field will ignore if it is null
     *
     * @param id the id of the questionKnowledgeLinkDTO to save.
     * @param questionKnowledgeLinkDTO the questionKnowledgeLinkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated questionKnowledgeLinkDTO,
     * or with status {@code 400 (Bad Request)} if the questionKnowledgeLinkDTO is not valid,
     * or with status {@code 404 (Not Found)} if the questionKnowledgeLinkDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the questionKnowledgeLinkDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<QuestionKnowledgeLinkDTO> partialUpdateQuestionKnowledgeLink(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody QuestionKnowledgeLinkDTO questionKnowledgeLinkDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update QuestionKnowledgeLink : {}, {}", id, questionKnowledgeLinkDTO);
        if (questionKnowledgeLinkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, questionKnowledgeLinkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!questionKnowledgeLinkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<QuestionKnowledgeLinkDTO> result = questionKnowledgeLinkService.partialUpdate(questionKnowledgeLinkDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, questionKnowledgeLinkDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /question-knowledge-links} : get all the Question Knowledge Links.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Question Knowledge Links in body.
     */
    @GetMapping("")
    public ResponseEntity<List<QuestionKnowledgeLinkDTO>> getAllQuestionKnowledgeLinks(
        QuestionKnowledgeLinkCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get QuestionKnowledgeLinks by criteria: {}", criteria);

        Page<QuestionKnowledgeLinkDTO> page = questionKnowledgeLinkQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /question-knowledge-links/count} : count all the questionKnowledgeLinks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countQuestionKnowledgeLinks(QuestionKnowledgeLinkCriteria criteria) {
        LOG.debug("REST request to count QuestionKnowledgeLinks by criteria: {}", criteria);
        return ResponseEntity.ok().body(questionKnowledgeLinkQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /question-knowledge-links/:id} : get the "id" questionKnowledgeLink.
     *
     * @param id the id of the questionKnowledgeLinkDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the questionKnowledgeLinkDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuestionKnowledgeLinkDTO> getQuestionKnowledgeLink(@PathVariable("id") Long id) {
        LOG.debug("REST request to get QuestionKnowledgeLink : {}", id);
        Optional<QuestionKnowledgeLinkDTO> questionKnowledgeLinkDTO = questionKnowledgeLinkService.findOne(id);
        return ResponseUtil.wrapOrNotFound(questionKnowledgeLinkDTO);
    }

    /**
     * {@code DELETE  /question-knowledge-links/:id} : delete the "id" questionKnowledgeLink.
     *
     * @param id the id of the questionKnowledgeLinkDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestionKnowledgeLink(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete QuestionKnowledgeLink : {}", id);
        questionKnowledgeLinkService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
