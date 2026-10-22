package com.lumi.web.rest;

import com.lumi.repository.ExamQuestionRepository;
import com.lumi.service.ExamQuestionQueryService;
import com.lumi.service.ExamQuestionService;
import com.lumi.service.criteria.ExamQuestionCriteria;
import com.lumi.service.dto.ExamQuestionDTO;
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
 * REST controller for managing {@link com.lumi.domain.ExamQuestion}.
 */
@RestController
@RequestMapping("/api/exam-questions")
public class ExamQuestionResource {

    private static final Logger LOG = LoggerFactory.getLogger(ExamQuestionResource.class);

    private static final String ENTITY_NAME = "examQuestion";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final ExamQuestionService examQuestionService;

    private final ExamQuestionRepository examQuestionRepository;

    private final ExamQuestionQueryService examQuestionQueryService;

    public ExamQuestionResource(
        ExamQuestionService examQuestionService,
        ExamQuestionRepository examQuestionRepository,
        ExamQuestionQueryService examQuestionQueryService
    ) {
        this.examQuestionService = examQuestionService;
        this.examQuestionRepository = examQuestionRepository;
        this.examQuestionQueryService = examQuestionQueryService;
    }

    /**
     * {@code POST  /exam-questions} : Create a new examQuestion.
     *
     * @param examQuestionDTO the examQuestionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new examQuestionDTO, or with status {@code 400 (Bad Request)} if the examQuestion already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ExamQuestionDTO> createExamQuestion(@Valid @RequestBody ExamQuestionDTO examQuestionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ExamQuestion : {}", examQuestionDTO);
        if (examQuestionDTO.getId() != null) {
            throw new BadRequestAlertException("A new examQuestion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        examQuestionDTO = examQuestionService.save(examQuestionDTO);
        return ResponseEntity.created(new URI("/api/exam-questions/" + examQuestionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, examQuestionDTO.getId().toString()))
            .body(examQuestionDTO);
    }

    /**
     * {@code PUT  /exam-questions/:id} : Updates an existing examQuestion.
     *
     * @param id the id of the examQuestionDTO to save.
     * @param examQuestionDTO the examQuestionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated examQuestionDTO,
     * or with status {@code 400 (Bad Request)} if the examQuestionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the examQuestionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExamQuestionDTO> updateExamQuestion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ExamQuestionDTO examQuestionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ExamQuestion : {}, {}", id, examQuestionDTO);
        if (examQuestionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, examQuestionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!examQuestionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        examQuestionDTO = examQuestionService.update(examQuestionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, examQuestionDTO.getId().toString()))
            .body(examQuestionDTO);
    }

    /**
     * {@code PATCH  /exam-questions/:id} : Partial updates given fields of an existing examQuestion, field will ignore if it is null
     *
     * @param id the id of the examQuestionDTO to save.
     * @param examQuestionDTO the examQuestionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated examQuestionDTO,
     * or with status {@code 400 (Bad Request)} if the examQuestionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the examQuestionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the examQuestionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ExamQuestionDTO> partialUpdateExamQuestion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ExamQuestionDTO examQuestionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update ExamQuestion : {}, {}", id, examQuestionDTO);
        if (examQuestionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, examQuestionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!examQuestionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ExamQuestionDTO> result = examQuestionService.partialUpdate(examQuestionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, examQuestionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /exam-questions} : get all the Exam Questions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Exam Questions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ExamQuestionDTO>> getAllExamQuestions(ExamQuestionCriteria criteria) {
        LOG.debug("REST request to get ExamQuestions by criteria: {}", criteria);

        List<ExamQuestionDTO> entityList = examQuestionQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /exam-questions/count} : count all the examQuestions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countExamQuestions(ExamQuestionCriteria criteria) {
        LOG.debug("REST request to count ExamQuestions by criteria: {}", criteria);
        return ResponseEntity.ok().body(examQuestionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /exam-questions/:id} : get the "id" examQuestion.
     *
     * @param id the id of the examQuestionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the examQuestionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExamQuestionDTO> getExamQuestion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ExamQuestion : {}", id);
        Optional<ExamQuestionDTO> examQuestionDTO = examQuestionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(examQuestionDTO);
    }

    /**
     * {@code DELETE  /exam-questions/:id} : delete the "id" examQuestion.
     *
     * @param id the id of the examQuestionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExamQuestion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ExamQuestion : {}", id);
        examQuestionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
