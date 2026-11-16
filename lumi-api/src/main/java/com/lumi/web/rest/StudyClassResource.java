package com.lumi.web.rest;

import com.lumi.domain.enumeration.RecordStatus;
import com.lumi.service.StudyClassService;
import com.lumi.service.dto.APIResponse;
import com.lumi.service.dto.PageResponseDTO;
import com.lumi.service.dto.studyClass.CreateStudyClassRequestDTO;
import com.lumi.service.dto.studyClass.InstructorClassListItemDTO;
import com.lumi.service.dto.studyClass.StudyClassDetailDTO;
import com.lumi.service.dto.studyClass.UpdateStudyClassRequestDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing {@link com.lumi.domain.StudyClass}.
 */
@RestController
@PreAuthorize("@instructorAccessService.canUseInstructorFeatures()")
@RequestMapping("/api")
public class StudyClassResource {

    private static final Logger LOG = LoggerFactory.getLogger(StudyClassResource.class);

    private final StudyClassService studyClassService;

    private static final String ENTITY_NAME = "studyClass";

    @Value("${jhipster.clientApp.name:lumi}")
    private String applicationName;

    public StudyClassResource(StudyClassService studyClassService) {
        this.studyClassService = studyClassService;
    }

    @GetMapping("/instructor/classes")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<PageResponseDTO<InstructorClassListItemDTO>>> ListClass(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) RecordStatus status
    ) {
        PageResponseDTO<InstructorClassListItemDTO> result = studyClassService.listClass(page, size, keyword, status);
        APIResponse<PageResponseDTO<InstructorClassListItemDTO>> body = APIResponse.<PageResponseDTO<InstructorClassListItemDTO>>builder()
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
            .message("Cập nhật lớp học thành công")
            .data(result)
            .build();

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @DeleteMapping("/instructor/classes/{id}")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<Void> deleteClass(@PathVariable("id") Long id) {
        studyClassService.deleteClass(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
