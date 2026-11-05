package com.lumi.web.rest;

import com.lumi.service.dto.InstructorCourseDTOs.Builder;
import com.lumi.service.dto.InstructorCourseDTOs.VideoDuration;
import com.lumi.service.impl.InstructorCourseMediaService;
import com.lumi.service.storage.CourseObjectStorage.Access;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Validated
@RequestMapping("/api/instructor/courses/{courseId}")
public class InstructorCourseMediaResource {

    private final InstructorCourseMediaService service;

    public InstructorCourseMediaResource(InstructorCourseMediaService service) {
        this.service = service;
    }

    @PostMapping(value = "/lessons/{lessonId}/materials", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Builder addMaterial(
        @PathVariable Long courseId,
        @PathVariable Long lessonId,
        @RequestParam @Min(0) Long version,
        @RequestPart("file") MultipartFile file
    ) {
        return service.uploadMaterial(courseId, lessonId, null, version, file);
    }

    @PutMapping(value = "/lessons/{lessonId}/materials/{materialId}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Builder replaceMaterial(
        @PathVariable Long courseId,
        @PathVariable Long lessonId,
        @PathVariable Long materialId,
        @RequestParam @Min(0) Long version,
        @RequestPart("file") MultipartFile file
    ) {
        return service.uploadMaterial(courseId, lessonId, materialId, version, file);
    }

    @PostMapping(value = "/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Builder thumbnail(@PathVariable Long courseId, @RequestParam @Min(0) Long version, @RequestPart("file") MultipartFile file) {
        return service.courseMedia(courseId, version, file, true);
    }

    @DeleteMapping("/thumbnail")
    public Builder removeThumbnail(@PathVariable Long courseId, @RequestParam @Min(0) Long version) {
        return service.removeCourseMedia(courseId, version, true);
    }

    @PostMapping(value = "/intro-video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Builder intro(@PathVariable Long courseId, @RequestParam @Min(0) Long version, @RequestPart("file") MultipartFile file) {
        return service.courseMedia(courseId, version, file, false);
    }

    @DeleteMapping("/intro-video")
    public Builder removeIntro(@PathVariable Long courseId, @RequestParam @Min(0) Long version) {
        return service.removeCourseMedia(courseId, version, false);
    }

    @PostMapping(value = "/lessons/{lessonId}/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Builder video(
        @PathVariable Long courseId,
        @PathVariable Long lessonId,
        @RequestParam @Min(0) Long version,
        @RequestParam(required = false) @Min(0) Integer durationSeconds,
        @RequestPart("file") MultipartFile file
    ) {
        return service.lessonVideo(courseId, lessonId, version, file, durationSeconds);
    }

    @DeleteMapping("/lessons/{lessonId}/video")
    public Builder removeVideo(@PathVariable Long courseId, @PathVariable Long lessonId, @RequestParam @Min(0) Long version) {
        return service.removeLessonVideo(courseId, lessonId, version);
    }

    @PutMapping("/lessons/{lessonId}/video/duration")
    public Builder duration(@PathVariable Long courseId, @PathVariable Long lessonId, @Valid @RequestBody VideoDuration input) {
        return service.updateVideoDuration(courseId, lessonId, input.version(), input.durationSeconds());
    }

    @GetMapping("/lessons/{lessonId}/materials/{materialId}/download")
    public ResponseEntity<Access> materialAccess(@PathVariable Long courseId, @PathVariable Long lessonId, @PathVariable Long materialId) {
        return noCache(service.materialAccess(courseId, lessonId, materialId));
    }

    @GetMapping("/lessons/{lessonId}/video/playback")
    public ResponseEntity<Access> videoAccess(@PathVariable Long courseId, @PathVariable Long lessonId) {
        return noCache(service.videoAccess(courseId, lessonId, false));
    }

    @GetMapping("/lessons/{lessonId}/video/download")
    public ResponseEntity<Access> videoDownload(@PathVariable Long courseId, @PathVariable Long lessonId) {
        return noCache(service.videoAccess(courseId, lessonId, true));
    }

    @GetMapping("/thumbnail/access")
    public ResponseEntity<Access> thumbnailAccess(@PathVariable Long courseId) {
        return noCache(service.courseMediaAccess(courseId, true));
    }

    @GetMapping("/intro-video/playback")
    public ResponseEntity<Access> introAccess(@PathVariable Long courseId) {
        return noCache(service.courseMediaAccess(courseId, false));
    }

    private ResponseEntity<Access> noCache(Access access) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(access);
    }
}
