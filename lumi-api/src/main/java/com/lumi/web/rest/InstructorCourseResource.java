package com.lumi.web.rest;

import com.lumi.service.InstructorCourseService;
import com.lumi.service.dto.InstructorCourseDTOs.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/instructor/courses")
public class InstructorCourseResource {

    private final InstructorCourseService service;

    public InstructorCourseResource(InstructorCourseService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Builder> create(@Valid @RequestBody(required = false) CreateDraft input) {
        Builder result = service.create(input);
        return ResponseEntity.created(URI.create("/api/instructor/courses/" + result.course().getId() + "/builder")).body(result);
    }

    @GetMapping("/{courseId}/builder")
    public Builder get(@PathVariable Long courseId) {
        return service.get(courseId);
    }

    @PutMapping("/{courseId}/basic-info")
    public Builder basicInfo(@PathVariable Long courseId, @Valid @RequestBody BasicInfo input) {
        return service.basicInfo(courseId, input);
    }

    @PutMapping("/{courseId}/category")
    public Builder category(@PathVariable Long courseId, @Valid @RequestBody CategoryInput input) {
        return service.category(courseId, input);
    }

    @GetMapping("/{courseId}/review")
    public ReviewState review(@PathVariable Long courseId) {
        return service.get(courseId).review();
    }

    @PostMapping("/{courseId}/review-requests")
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public Builder submitReview(@PathVariable Long courseId, @Valid @RequestBody SubmitReview input) {
        return service.submitReview(courseId, input);
    }

    @PutMapping("/{courseId}/pricing")
    public Builder pricing(@PathVariable Long courseId, @Valid @RequestBody Pricing input) {
        return service.pricing(courseId, input);
    }

    @PostMapping("/{courseId}/chapters")
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public Builder addChapter(@PathVariable Long courseId, @Valid @RequestBody ChapterInput input) {
        return service.addChapter(courseId, input);
    }

    @PutMapping("/{courseId}/chapters/{chapterId}")
    public Builder updateChapter(@PathVariable Long courseId, @PathVariable Long chapterId, @Valid @RequestBody ChapterInput input) {
        return service.updateChapter(courseId, chapterId, input);
    }

    @DeleteMapping("/{courseId}/chapters/{chapterId}")
    public Builder deleteChapter(@PathVariable Long courseId, @PathVariable Long chapterId, @RequestParam @Min(0) Long version) {
        return service.deleteChapter(courseId, chapterId, version);
    }

    @PostMapping("/{courseId}/chapters/{chapterId}/lessons")
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public Builder addLesson(@PathVariable Long courseId, @PathVariable Long chapterId, @Valid @RequestBody LessonInput input) {
        return service.addLesson(courseId, chapterId, input);
    }

    @PutMapping("/{courseId}/lessons/{lessonId}")
    public Builder updateLesson(@PathVariable Long courseId, @PathVariable Long lessonId, @Valid @RequestBody LessonInput input) {
        return service.updateLesson(courseId, lessonId, input);
    }

    @DeleteMapping("/{courseId}/lessons/{lessonId}")
    public Builder deleteLesson(@PathVariable Long courseId, @PathVariable Long lessonId, @RequestParam @Min(0) Long version) {
        return service.deleteLesson(courseId, lessonId, version);
    }

    @PutMapping("/{courseId}/curriculum/order")
    public Builder reorder(@PathVariable Long courseId, @Valid @RequestBody CurriculumOrder input) {
        return service.reorder(courseId, input);
    }

    @PutMapping("/{courseId}/lessons/{lessonId}/move")
    public Builder moveLesson(@PathVariable Long courseId, @PathVariable Long lessonId, @Valid @RequestBody MoveLesson input) {
        return service.moveLesson(courseId, lessonId, input);
    }

    @PutMapping("/{courseId}/lessons/{lessonId}/materials/{materialId}")
    public Builder renameMaterial(
        @PathVariable Long courseId,
        @PathVariable Long lessonId,
        @PathVariable Long materialId,
        @Valid @RequestBody MaterialInput input
    ) {
        return service.renameMaterial(courseId, lessonId, materialId, input);
    }

    @PutMapping("/{courseId}/lessons/{lessonId}/materials/order")
    public Builder reorderMaterials(@PathVariable Long courseId, @PathVariable Long lessonId, @Valid @RequestBody MaterialOrder input) {
        return service.reorderMaterials(courseId, lessonId, input);
    }

    @DeleteMapping("/{courseId}/lessons/{lessonId}/materials/{materialId}")
    public Builder deleteMaterial(
        @PathVariable Long courseId,
        @PathVariable Long lessonId,
        @PathVariable Long materialId,
        @RequestParam @Min(0) Long version
    ) {
        return service.deleteMaterial(courseId, lessonId, materialId, version);
    }
}
