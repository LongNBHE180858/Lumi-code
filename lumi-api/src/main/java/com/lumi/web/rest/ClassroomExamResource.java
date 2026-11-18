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

    // ==================== 1. GIÁO VIÊN ====================

    // Lấy danh sách đề của lớp
    @GetMapping("/instructor/classes/{classId}/exams")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<List<ClassExamDTO.Summary>>> instructorExams(@PathVariable Long classId) {
        return result(HttpStatus.OK, service.instructorExams(classId));
    }

    // Lấy chi tiết đề để chỉnh sửa
    @GetMapping("/instructor/classes/{classId}/exams/{examId}")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<ClassExamDTO.Editor>> instructorExam(@PathVariable Long classId, @PathVariable Long examId) {
        return result(HttpStatus.OK, service.instructorExam(classId, examId));
    }

    // Tạo đề thủ công (nhập tay từng câu)
    @PostMapping("/instructor/classes/{classId}/exams")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<ClassExamDTO.Summary>> create(
        @PathVariable Long classId,
        @Valid @RequestBody ClassExamDTO.SaveExam input
    ) {
        return result(HttpStatus.CREATED, service.createExam(classId, input));
    }

    // Bốc ngẫu nhiên câu hỏi theo ma trận để xem trước
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

    @PatchMapping("/instructor/classes/{classId}/exams/{examId}/status")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<ClassExamDTO.Summary>> updateStatus(
        @PathVariable Long classId,
        @PathVariable Long examId,
        @Valid @RequestBody ClassExamDTO.UpdateStatus input
    ) {
        return result(HttpStatus.OK, service.updateStatus(classId, examId, input));
    }

    // Xem tiến độ, điểm trung bình và câu sai của cả lớp
    @GetMapping("/instructor/classes/{classId}/progress")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<ClassExamDTO.Progress>> progress(@PathVariable Long classId) {
        return result(HttpStatus.OK, service.progress(classId));
    }

    // Lấy cấu hình quy tắc mặc định theo loại bài (EXAM / PRACTICE)
    @GetMapping("/assessment-rules/defaults")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<ClassExamDTO.AssessmentRulesConfig>> getAssessmentRuleDefaults(
        @RequestParam(required = false, defaultValue = "EXAM") String type
    ) {
        return result(HttpStatus.OK, service.getAssessmentRuleDefaults(type));
    }

    // ==================== 2. HỌC SINH ====================

    // Học sinh xem danh sách đề đang mở của lớp
    @GetMapping("/student/classes/{classId}/exams")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<APIResponse<List<ClassExamDTO.StudentExamSummary>>> studentExams(@PathVariable Long classId) {
        return result(HttpStatus.OK, service.studentExams(classId));
    }

    // Bắt đầu làm bài (hoặc lấy lại bài đang làm dở)
    @PostMapping("/student/classes/{classId}/exams/{examId}/attempts")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<APIResponse<ClassExamDTO.AttemptView>> start(@PathVariable Long classId, @PathVariable Long examId) {
        return result(HttpStatus.OK, service.startAttempt(classId, examId));
    }

    @PutMapping("/student/classes/{classId}/exams/{examId}/attempts/{attemptId}/answers")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<APIResponse<ClassExamDTO.SaveDraftResult>> saveDraft(
        @PathVariable Long classId,
        @PathVariable Long examId,
        @PathVariable Long attemptId,
        @Valid @RequestBody ClassExamDTO.Submit input
    ) {
        return result(HttpStatus.OK, service.saveDraftAnswers(classId, examId, attemptId, input));
    }

    // Nộp bài thi và tự động chấm điểm
    @PostMapping("/student/classes/{classId}/exams/{examId}/attempts/{attemptId}/submit")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<APIResponse<ClassExamDTO.Result>> submit(
        @PathVariable Long classId,
        @PathVariable Long examId,
        @PathVariable Long attemptId,
        @Valid @RequestBody(required = false) ClassExamDTO.SubmitExam input
    ) {
        return result(HttpStatus.OK, service.submit(classId, examId, attemptId, input));
    }

    // Xem kết quả chi tiết của một lượt làm bài
    @GetMapping("/student/classes/{classId}/exams/{examId}/attempts/{attemptId}/result")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<APIResponse<ClassExamDTO.Result>> studentResult(
        @PathVariable Long classId,
        @PathVariable Long examId,
        @PathVariable Long attemptId
    ) {
        return result(HttpStatus.OK, service.studentResult(classId, examId, attemptId));
    }

    // Xem lịch sử các lần nộp bài của một đề
    @GetMapping("/student/classes/{classId}/exams/{examId}/results")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<APIResponse<List<ClassExamDTO.Result>>> studentResults(@PathVariable Long classId, @PathVariable Long examId) {
        return result(HttpStatus.OK, service.studentResults(classId, examId));
    }
}
