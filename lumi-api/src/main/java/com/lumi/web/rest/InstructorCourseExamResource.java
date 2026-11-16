package com.lumi.web.rest;

import com.lumi.service.dto.InstructorExamDTOs.*;
import com.lumi.service.impl.InstructorCourseExamService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/instructor/courses/{courseId}")
public class InstructorCourseExamResource {

    private final InstructorCourseExamService examService;

    public InstructorCourseExamResource(InstructorCourseExamService examService) {
        this.examService = examService;
    }

    // =========================================================================
    // 1. Course-level Exams (Đề kiểm tra khóa học)
    // =========================================================================

    @GetMapping("/exams")
    public ResponseEntity<List<ExamSummaryDTO>> listCourseExams(@PathVariable Long courseId) {
        return ResponseEntity.ok(examService.listCourseExams(courseId));
    }

    @PostMapping("/exams")
    public ResponseEntity<ExamDetailDTO> createCourseExam(@PathVariable Long courseId, @Valid @RequestBody CreateExamInput input) {
        ExamDetailDTO result = examService.createCourseExam(courseId, input);
        return ResponseEntity.created(URI.create("/api/instructor/courses/" + courseId + "/exams/" + result.id())).body(result);
    }

    // =========================================================================
    // 2. Chapter-level Quizzes (Bài kiểm tra chương)
    // =========================================================================

    @GetMapping("/chapters/{chapterId}/exams")
    public ResponseEntity<List<ExamSummaryDTO>> listChapterExams(@PathVariable Long courseId, @PathVariable Long chapterId) {
        return ResponseEntity.ok(examService.listChapterExams(courseId, chapterId));
    }

    @PostMapping("/chapters/{chapterId}/exams")
    public ResponseEntity<ExamDetailDTO> createChapterExam(
        @PathVariable Long courseId,
        @PathVariable Long chapterId,
        @Valid @RequestBody CreateExamInput input
    ) {
        ExamDetailDTO result = examService.createChapterExam(courseId, chapterId, input);
        return ResponseEntity.created(URI.create("/api/instructor/courses/" + courseId + "/exams/" + result.id())).body(result);
    }

    // =========================================================================
    // 3. Lesson-level Quizzes (Bài tập ôn tập cuối bài học)
    // =========================================================================

    @GetMapping({ "/lessons/{lessonId}/exams", "/lessons/{lessonId}/quizzes" })
    public ResponseEntity<List<ExamSummaryDTO>> listLessonQuizzes(@PathVariable Long courseId, @PathVariable Long lessonId) {
        return ResponseEntity.ok(examService.listLessonQuizzes(courseId, lessonId));
    }

    @PostMapping({ "/lessons/{lessonId}/exams", "/lessons/{lessonId}/quizzes" })
    public ResponseEntity<ExamDetailDTO> createLessonQuiz(
        @PathVariable Long courseId,
        @PathVariable Long lessonId,
        @RequestParam(name = "practice", required = false) Boolean practice,
        @Valid @RequestBody CreateExamInput input
    ) {
        ExamDetailDTO result = examService.createLessonQuiz(courseId, lessonId, input, practice);
        return ResponseEntity.created(URI.create("/api/instructor/courses/" + courseId + "/exams/" + result.id())).body(result);
    }

    // =========================================================================
    // 4. Video Cue-Points (Câu hỏi tương tác dừng video)
    // =========================================================================

    @GetMapping("/lessons/{lessonId}/cue-points")
    public ResponseEntity<List<ExamSummaryDTO>> listLessonCuePoints(@PathVariable Long courseId, @PathVariable Long lessonId) {
        return ResponseEntity.ok(examService.listLessonCuePoints(courseId, lessonId));
    }

    @PostMapping("/lessons/{lessonId}/cue-points")
    public ResponseEntity<ExamDetailDTO> createLessonCuePoint(
        @PathVariable Long courseId,
        @PathVariable Long lessonId,
        @Valid @RequestBody CreateExamInput input
    ) {
        ExamDetailDTO result = examService.createLessonCuePoint(courseId, lessonId, input);
        return ResponseEntity.created(URI.create("/api/instructor/courses/" + courseId + "/exams/" + result.id())).body(result);
    }

    // =========================================================================
    // 5. Exam General CRUD (Detail, Update, Delete)
    // =========================================================================

    @GetMapping("/exams/{examId}")
    public ResponseEntity<ExamDetailDTO> getExamDetail(@PathVariable Long courseId, @PathVariable Long examId) {
        return ResponseEntity.ok(examService.getExamDetail(courseId, examId));
    }

    @PutMapping("/exams/{examId}")
    public ResponseEntity<ExamDetailDTO> updateExam(
        @PathVariable Long courseId,
        @PathVariable Long examId,
        @Valid @RequestBody UpdateExamInput input
    ) {
        return ResponseEntity.ok(examService.updateExam(courseId, examId, input));
    }

    @DeleteMapping("/exams/{examId}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long courseId, @PathVariable Long examId) {
        examService.deleteExam(courseId, examId);
        return ResponseEntity.noContent().build();
    }

    // =========================================================================
    // 6. Question & Option Management inside Exam
    // =========================================================================

    @PostMapping("/exams/{examId}/questions")
    public ResponseEntity<ExamDetailDTO> addQuestionToExam(
        @PathVariable Long courseId,
        @PathVariable Long examId,
        @Valid @RequestBody QuestionInput input
    ) {
        return ResponseEntity.ok(examService.addQuestionToExam(courseId, examId, input));
    }

    @PutMapping("/exams/{examId}/questions/{questionId}")
    public ResponseEntity<ExamDetailDTO> updateQuestionInExam(
        @PathVariable Long courseId,
        @PathVariable Long examId,
        @PathVariable Long questionId,
        @Valid @RequestBody QuestionInput input
    ) {
        return ResponseEntity.ok(examService.updateQuestionInExam(courseId, examId, questionId, input));
    }

    @DeleteMapping("/exams/{examId}/questions/{questionId}")
    public ResponseEntity<ExamDetailDTO> deleteQuestionFromExam(
        @PathVariable Long courseId,
        @PathVariable Long examId,
        @PathVariable Long questionId
    ) {
        return ResponseEntity.ok(examService.deleteQuestionFromExam(courseId, examId, questionId));
    }
}
