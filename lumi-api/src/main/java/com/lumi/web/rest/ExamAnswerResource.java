package com.lumi.web.rest;

import com.lumi.repository.ExamAnswerRepository;
import com.lumi.service.ExamAnswerQueryService;
import com.lumi.service.ExamAnswerService;
import com.lumi.service.criteria.ExamAnswerCriteria;
import com.lumi.service.dto.ExamAnswerDTO;
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
 * REST controller for managing {@link com.lumi.domain.ExamAnswer}.
 */
@RestController
@RequestMapping("/api/exam-answers")
public class ExamAnswerResource {

    private static final Logger LOG = LoggerFactory.getLogger(ExamAnswerResource.class);

    private static final String ENTITY_NAME = "examAnswer";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final ExamAnswerService examAnswerService;

    private final ExamAnswerRepository examAnswerRepository;

    private final ExamAnswerQueryService examAnswerQueryService;

    public ExamAnswerResource(
        ExamAnswerService examAnswerService,
        ExamAnswerRepository examAnswerRepository,
        ExamAnswerQueryService examAnswerQueryService
    ) {
        this.examAnswerService = examAnswerService;
        this.examAnswerRepository = examAnswerRepository;
        this.examAnswerQueryService = examAnswerQueryService;
    }

    /**
     * {@code POST  /exam-answers} : Create a new examAnswer.
     *
     * @param examAnswerDTO the examAnswerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new examAnswerDTO, or with status {@code 400 (Bad Request)} if the examAnswer already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ExamAnswerDTO> createExamAnswer(@Valid @RequestBody ExamAnswerDTO examAnswerDTO) throws URISyntaxException {
        LOG.debug("REST request to save ExamAnswer : {}", examAnswerDTO);
        if (examAnswerDTO.getId() != null) {
            throw new BadRequestAlertException("A new examAnswer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        examAnswerDTO = examAnswerService.save(examAnswerDTO);
        return ResponseEntity.created(new URI("/api/exam-answers/" + examAnswerDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, examAnswerDTO.getId().toString()))
            .body(examAnswerDTO);
    }

    /**
     * {@code PUT  /exam-answers/:id} : Updates an existing examAnswer.
     *
     * @param id the id of the examAnswerDTO to save.
     * @param examAnswerDTO the examAnswerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated examAnswerDTO,
     * or with status {@code 400 (Bad Request)} if the examAnswerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the examAnswerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExamAnswerDTO> updateExamAnswer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ExamAnswerDTO examAnswerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ExamAnswer : {}, {}", id, examAnswerDTO);
        if (examAnswerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, examAnswerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!examAnswerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        examAnswerDTO = examAnswerService.update(examAnswerDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, examAnswerDTO.getId().toString()))
            .body(examAnswerDTO);
    }

    /**
     * {@code PATCH  /exam-answers/:id} : Partial updates given fields of an existing examAnswer, field will ignore if it is null
     *
     * @param id the id of the examAnswerDTO to save.
     * @param examAnswerDTO the examAnswerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated examAnswerDTO,
     * or with status {@code 400 (Bad Request)} if the examAnswerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the examAnswerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the examAnswerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ExamAnswerDTO> partialUpdateExamAnswer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ExamAnswerDTO examAnswerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update ExamAnswer : {}, {}", id, examAnswerDTO);
        if (examAnswerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, examAnswerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!examAnswerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ExamAnswerDTO> result = examAnswerService.partialUpdate(examAnswerDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, examAnswerDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /exam-answers} : get all the Exam Answers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Exam Answers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ExamAnswerDTO>> getAllExamAnswers(
        ExamAnswerCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ExamAnswers by criteria: {}", criteria);

        Page<ExamAnswerDTO> page = examAnswerQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /exam-answers/count} : count all the examAnswers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countExamAnswers(ExamAnswerCriteria criteria) {
        LOG.debug("REST request to count ExamAnswers by criteria: {}", criteria);
        return ResponseEntity.ok().body(examAnswerQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /exam-answers/:id} : get the "id" examAnswer.
     *
     * @param id the id of the examAnswerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the examAnswerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExamAnswerDTO> getExamAnswer(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ExamAnswer : {}", id);
        Optional<ExamAnswerDTO> examAnswerDTO = examAnswerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(examAnswerDTO);
    }

    /**
     * {@code DELETE  /exam-answers/:id} : delete the "id" examAnswer.
     *
     * @param id the id of the examAnswerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExamAnswer(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ExamAnswer : {}", id);
        examAnswerService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
