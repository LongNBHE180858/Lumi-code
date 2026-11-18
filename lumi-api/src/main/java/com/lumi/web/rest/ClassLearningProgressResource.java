package com.lumi.web.rest;

import com.lumi.service.ClassLearningProgressService;
import com.lumi.service.dto.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/instructor/classes/{classId}/learning-progress")
@PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
public class ClassLearningProgressResource {

    private final ClassLearningProgressService service;

    public ClassLearningProgressResource(ClassLearningProgressService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<APIResponse<ClassLearningProgressService.Progress>> progress(@PathVariable Long classId) {
        return ResponseEntity.ok(
            APIResponse.<ClassLearningProgressService.Progress>builder()
                .status(200)
                .code(1000)
                .message("Thành công")
                .data(service.progress(classId))
                .build()
        );
    }
}
