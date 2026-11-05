package com.lumi.web.rest;

import com.lumi.repository.StudyClassRepository;
import com.lumi.service.StudyClassService;
import com.lumi.service.criteria.StudyClassCriteria;
import com.lumi.service.dto.APIResponse;
import com.lumi.service.dto.StudyClassDTO;
import com.lumi.service.dto.studyClass.CreateStudyClassRequestDTO;
import com.lumi.service.dto.studyClass.StudyClassDetailDTO;
import com.lumi.service.dto.studyClass.UpdateStudyClassRequestDTO;
import com.lumi.service.query.StudyClassQueryService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.lumi.domain.StudyClass}.
 */
@RestController
@RequestMapping("/api")
public class StudyClassResource {

    private static final Logger LOG = LoggerFactory.getLogger(StudyClassResource.class);

    private static final String ENTITY_NAME = "studyClass";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    private final StudyClassService studyClassService;

    private final StudyClassRepository studyClassRepository;

    private final StudyClassQueryService studyClassQueryService;

    public StudyClassResource(
        StudyClassService studyClassService,
        StudyClassRepository studyClassRepository,
        StudyClassQueryService studyClassQueryService
    ) {
        this.studyClassService = studyClassService;
        this.studyClassRepository = studyClassRepository;
        this.studyClassQueryService = studyClassQueryService;
    }

    @GetMapping("/instructor/classes")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<List<StudyClassDetailDTO>>> ListClass() {
        List<StudyClassDetailDTO> result = studyClassService.listClass();
        APIResponse<List<StudyClassDetailDTO>> body = APIResponse.<List<StudyClassDetailDTO>>builder()
            .status(HttpStatus.OK.value())
            .code(1000)
            .message("Lấy danh sách lớp học thành công")
            .data(result)
            .build();

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PostMapping("/instructor/classes")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<StudyClassDetailDTO>> createClass(@Valid @RequestBody CreateStudyClassRequestDTO request) {
        StudyClassDetailDTO result = studyClassService.createClass(request);

        APIResponse<StudyClassDetailDTO> body = APIResponse.<StudyClassDetailDTO>builder()
            .status(HttpStatus.CREATED.value())
            .code(1000)
            .message("Tạo lớp học thành công")
            .data(result)
            .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PutMapping("/instructor/classes/{id}")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<StudyClassDetailDTO>> updateClass(
        @PathVariable(value = "id", required = true) final Long id,
        @Valid @RequestBody UpdateStudyClassRequestDTO request
    ) {
        StudyClassDetailDTO result = studyClassService.updateClass(id, request);
        APIResponse<StudyClassDetailDTO> body = APIResponse.<StudyClassDetailDTO>builder()
            .status(HttpStatus.OK.value())
            .code(1000)
            .message("Tạo lớp học thành công")
            .data(result)
            .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PostMapping("")
    public ResponseEntity<StudyClassDTO> createStudyClass(@Valid @RequestBody StudyClassDTO studyClassDTO) throws URISyntaxException {
        LOG.debug("REST request to save StudyClass : {}", studyClassDTO);
        if (studyClassDTO.getId() != null) {
            throw new BadRequestAlertException("A new studyClass cannot already have an ID", ENTITY_NAME, "idexists");
        }
        studyClassDTO = studyClassService.save(studyClassDTO);
        return ResponseEntity.created(new URI("/api/study-classes/" + studyClassDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, studyClassDTO.getId().toString()))
            .body(studyClassDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudyClassDTO> updateStudyClass(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StudyClassDTO studyClassDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StudyClass : {}, {}", id, studyClassDTO);
        if (studyClassDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studyClassDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!studyClassRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        studyClassDTO = studyClassService.update(studyClassDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, studyClassDTO.getId().toString()))
            .body(studyClassDTO);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StudyClassDTO> partialUpdateStudyClass(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StudyClassDTO studyClassDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update StudyClass : {}, {}", id, studyClassDTO);
        if (studyClassDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studyClassDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!studyClassRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StudyClassDTO> result = studyClassService.partialUpdate(studyClassDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, studyClassDTO.getId().toString())
        );
    }

    @GetMapping("")
    public ResponseEntity<List<StudyClassDTO>> getAllStudyClasses(
        StudyClassCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get StudyClasses by criteria: {}", criteria);

        Page<StudyClassDTO> page = studyClassQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countStudyClasses(StudyClassCriteria criteria) {
        LOG.debug("REST request to count StudyClasses by criteria: {}", criteria);
        return ResponseEntity.ok().body(studyClassQueryService.countByCriteria(criteria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudyClassDTO> getStudyClass(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StudyClass : {}", id);
        Optional<StudyClassDTO> studyClassDTO = studyClassService.findOne(id);
        return ResponseUtil.wrapOrNotFound(studyClassDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudyClass(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StudyClass : {}", id);
        studyClassService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
