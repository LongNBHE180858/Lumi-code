package com.lumi.web.rest;

import com.lumi.service.ClassroomExamService;
import com.lumi.service.dto.APIResponse;
import com.lumi.service.dto.studyClass.ClassExamDTO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ClassroomExamResource {

    private final ClassroomExamService service;

    public ClassroomExamResource(ClassroomExamService service) {
        this.service = service;
    }

    private <T> ResponseEntity<APIResponse<T>> result(HttpStatus status, T data) {
        return ResponseEntity.status(status).body(
            APIResponse.<T>builder().status(status.value()).code(1000).message("Thành công").data(data).build()
        );
    }

    @GetMapping("/instructor/classes/{classId}/exams")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<List<ClassExamDTO.Summary>>> instructorExams(@PathVariable Long classId) {
        return result(HttpStatus.OK, service.instructorExams(classId));
    }

    @GetMapping("/instructor/classes/{classId}/exams/{examId}")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<ClassExamDTO.Editor>> instructorExam(@PathVariable Long classId, @PathVariable Long examId) {
        return result(HttpStatus.OK, service.instructorExam(classId, examId));
    }

    @PostMapping("/instructor/classes/{classId}/exams")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<ClassExamDTO.Summary>> create(
        @PathVariable Long classId,
        @Valid @RequestBody ClassExamDTO.SaveExam input
    ) {
        return result(HttpStatus.CREATED, service.createExam(classId, input));
    }

    @PostMapping("/instructor/classes/{classId}/exams/generate-preview")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<List<ClassExamDTO.PickedQuestion>>> generatePreview(
        @PathVariable Long classId,
        @Valid @RequestBody ClassExamDTO.GeneratePreviewRequest input
    ) {
        return result(HttpStatus.OK, service.generatePreview(classId, input));
    }

    @PostMapping("/instructor/classes/{classId}/exams/from-matrix")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<ClassExamDTO.Summary>> createFromMatrix(
        @PathVariable Long classId,
        @Valid @RequestBody ClassExamDTO.CreateFromMatrixRequest input
    ) {
        return result(HttpStatus.CREATED, service.createExamFromMatrix(classId, input));
    }

    @PutMapping("/instructor/classes/{classId}/exams/{examId}")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<ClassExamDTO.Summary>> update(
        @PathVariable Long classId,
        @PathVariable Long examId,
        @Valid @RequestBody ClassExamDTO.UpdateExam input
    ) {
        return result(HttpStatus.OK, service.updateExam(classId, examId, input));
    }

    @GetMapping("/instructor/classes/{classId}/progress")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<ClassExamDTO.Progress>> progress(@PathVariable Long classId) {
        return result(HttpStatus.OK, service.progress(classId));
    }

    @GetMapping("/student/classes/{classId}/exams")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<APIResponse<List<ClassExamDTO.Summary>>> studentExams(@PathVariable Long classId) {
        return result(HttpStatus.OK, service.studentExams(classId));
    }

    @PostMapping("/student/classes/{classId}/exams/{examId}/attempts")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<APIResponse<ClassExamDTO.AttemptView>> start(@PathVariable Long classId, @PathVariable Long examId) {
        return result(HttpStatus.OK, service.startAttempt(classId, examId));
    }

    @PostMapping("/student/classes/{classId}/exams/{examId}/attempts/{attemptId}/submit")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<APIResponse<ClassExamDTO.Result>> submit(
        @PathVariable Long classId,
        @PathVariable Long examId,
        @PathVariable Long attemptId,
        @Valid @RequestBody ClassExamDTO.Submit input
    ) {
        return result(HttpStatus.OK, service.submit(classId, examId, attemptId, input));
    }

    @GetMapping("/student/classes/{classId}/exams/{examId}/attempts/{attemptId}/result")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<APIResponse<ClassExamDTO.Result>> studentResult(
        @PathVariable Long classId,
        @PathVariable Long examId,
        @PathVariable Long attemptId
    ) {
        return result(HttpStatus.OK, service.studentResult(classId, examId, attemptId));
    }

    @GetMapping("/student/classes/{classId}/exams/{examId}/results")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<APIResponse<List<ClassExamDTO.Result>>> studentResults(@PathVariable Long classId, @PathVariable Long examId) {
        return result(HttpStatus.OK, service.studentResults(classId, examId));
    }
}
