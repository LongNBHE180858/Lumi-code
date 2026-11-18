package com.lumi.web.rest;

import com.lumi.service.ClassAssignmentService;
import com.lumi.service.dto.APIResponse;
import com.lumi.service.dto.studyClass.ClassAssignmentDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ClassAssignmentResource {

    private final ClassAssignmentService service;

    public ClassAssignmentResource(ClassAssignmentService service) {
        this.service = service;
    }

    private <T> ResponseEntity<APIResponse<T>> result(HttpStatus status, T data) {
        return ResponseEntity.status(status).body(
            APIResponse.<T>builder().status(status.value()).code(1000).message("Thành công").data(data).build()
        );
    }

    @GetMapping("/classes/{classId}/assignments/{assignmentId}/file/access")
    @PreAuthorize("hasAnyAuthority('ROLE_INSTRUCTOR', 'ROLE_STUDENT')")
    public ResponseEntity<APIResponse<com.lumi.service.storage.ClassroomObjectStorage.Access>> fileAccess(
        @PathVariable Long classId,
        @PathVariable Long assignmentId
    ) {
        return result(HttpStatus.OK, service.fileAccess(classId, assignmentId));
    }

    @GetMapping("/student/classes/{classId}/assignments/{assignmentId}/submission")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<APIResponse<ClassAssignmentDTO.Submission>> mine(@PathVariable Long classId, @PathVariable Long assignmentId) {
        return result(HttpStatus.OK, service.mine(classId, assignmentId));
    }

    @PostMapping("/student/classes/{classId}/assignments/{assignmentId}/submission")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<APIResponse<ClassAssignmentDTO.Submission>> submit(
        @PathVariable Long classId,
        @PathVariable Long assignmentId,
        @Valid @RequestBody ClassAssignmentDTO.Submit input
    ) {
        return result(HttpStatus.CREATED, service.submit(classId, assignmentId, input));
    }

    @PostMapping(
        value = "/student/classes/{classId}/assignments/{assignmentId}/submission/with-file",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<APIResponse<ClassAssignmentDTO.Submission>> submitWithFile(
        @PathVariable Long classId,
        @PathVariable Long assignmentId,
        @RequestParam(required = false) String content,
        @RequestPart("file") MultipartFile file
    ) {
        return result(HttpStatus.CREATED, service.submitWithFile(classId, assignmentId, content, file));
    }

    @GetMapping("/classes/{classId}/assignments/{assignmentId}/submissions/{submissionId}/file/access")
    @PreAuthorize("hasAnyAuthority('ROLE_INSTRUCTOR', 'ROLE_STUDENT')")
    public ResponseEntity<APIResponse<com.lumi.service.storage.ClassroomObjectStorage.Access>> submissionFileAccess(
        @PathVariable Long classId,
        @PathVariable Long assignmentId,
        @PathVariable Long submissionId
    ) {
        return result(HttpStatus.OK, service.submissionFileAccess(classId, assignmentId, submissionId));
    }

    @GetMapping("/instructor/classes/{classId}/assignments/{assignmentId}/submissions")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<ClassAssignmentDTO.SubmissionList>> submissions(
        @PathVariable Long classId,
        @PathVariable Long assignmentId
    ) {
        return result(HttpStatus.OK, service.instructorSubmissions(classId, assignmentId));
    }

    @PutMapping("/instructor/classes/{classId}/assignments/{assignmentId}/submissions/{submissionId}/grade")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<ClassAssignmentDTO.Submission>> grade(
        @PathVariable Long classId,
        @PathVariable Long assignmentId,
        @PathVariable Long submissionId,
        @Valid @RequestBody ClassAssignmentDTO.Grade input
    ) {
        return result(HttpStatus.OK, service.grade(classId, assignmentId, submissionId, input));
    }
}
