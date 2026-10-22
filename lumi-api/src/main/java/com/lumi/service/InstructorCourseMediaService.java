package com.lumi.service;

import com.lumi.domain.*;
import com.lumi.repository.CourseMaterialRepository;
import com.lumi.service.dto.InstructorCourseDTOs.Builder;
import com.lumi.service.storage.*;
import com.lumi.service.storage.CourseFileValidator.Kind;
import jakarta.persistence.EntityManager;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class InstructorCourseMediaService {

    private final InstructorCourseService courses;
    private final CourseMaterialRepository materials;
    private final CourseFileValidator validator;
    private final CourseObjectStorage storage;
    private final CourseStorageCleanup cleanup;
    private final EntityManager em;

    public InstructorCourseMediaService(
        InstructorCourseService courses,
        CourseMaterialRepository materials,
        CourseFileValidator validator,
        CourseObjectStorage storage,
        CourseStorageCleanup cleanup,
        EntityManager em
    ) {
        this.courses = courses;
        this.materials = materials;
        this.validator = validator;
        this.storage = storage;
        this.cleanup = cleanup;
        this.em = em;
    }

    public Builder uploadMaterial(Long courseId, Long lessonId, Long materialId, Long version, MultipartFile file) {
        Course course = courses.editable(courseId, version);
        Lesson lesson = courses.lesson(courseId, lessonId);
        CourseMaterial material =
            materialId == null ? new CourseMaterial().lesson(lesson) : courses.material(courseId, lessonId, materialId);
        var upload = upload(courseId, Kind.DOCUMENT, file);
        cleanup.onRollback(upload.url());
        if (materialId != null) {
            cleanup.afterCommit(material.getFileUrl());
            em.createQuery("delete from PdfChunk p where p.material.id = :id").setParameter("id", materialId).executeUpdate();
        } else {
            int next =
                materials
                    .findByLessonIdOrderByOrderIndexAscIdAsc(lessonId)
                    .stream()
                    .map(CourseMaterial::getOrderIndex)
                    .filter(java.util.Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(-1) + 1;
            material.setOrderIndex(next);
        }
        material.setFileUrl(upload.url());
        material.setFileName(upload.fileName());
        material.setMimeType(upload.mimeType());
        material.setFileSizeBytes(upload.size());
        material.setFileType(upload.mimeType().equals("application/pdf") ? "PDF" : "DOCX");
        materials.save(material);
        return courses.changed(course);
    }

    public Builder courseMedia(Long courseId, Long version, MultipartFile file, boolean thumbnail) {
        Course course = courses.editable(courseId, version);
        var upload = upload(courseId, thumbnail ? Kind.THUMBNAIL : Kind.VIDEO, file);
        cleanup.onRollback(upload.url());
        cleanup.afterCommit(thumbnail ? course.getThumbnailUrl() : course.getIntroVideoUrl());
        if (thumbnail) course.setThumbnailUrl(upload.url());
        else course.setIntroVideoUrl(upload.url());
        return courses.changed(course);
    }

    public Builder removeCourseMedia(Long courseId, Long version, boolean thumbnail) {
        Course course = courses.editable(courseId, version);
        cleanup.afterCommit(thumbnail ? course.getThumbnailUrl() : course.getIntroVideoUrl());
        if (thumbnail) course.setThumbnailUrl(null);
        else course.setIntroVideoUrl(null);
        return courses.changed(course);
    }

    public Builder lessonVideo(Long courseId, Long lessonId, Long version, MultipartFile file, Integer durationSeconds) {
        Course course = courses.editable(courseId, version);
        Lesson lesson = courses.lesson(courseId, lessonId);
        var upload = upload(courseId, Kind.VIDEO, file);
        cleanup.onRollback(upload.url());
        cleanup.afterCommit(lesson.getVideoUrl());
        lesson.setVideoUrl(upload.url());
        lesson.setVideoFileName(upload.fileName());
        lesson.setVideoSizeBytes(upload.size());
        // Thời lượng do trình duyệt đọc, chỉ dùng để hiển thị, không dùng cho điều kiện nghiệp vụ.
        lesson.setDurationSeconds(durationSeconds);
        lesson.setDurationMinutes(durationSeconds == null ? null : durationSeconds / 60);
        return courses.changed(course);
    }

    public Builder removeLessonVideo(Long courseId, Long lessonId, Long version) {
        Course course = courses.editable(courseId, version);
        Lesson lesson = courses.lesson(courseId, lessonId);
        cleanup.afterCommit(lesson.getVideoUrl());
        lesson.setVideoUrl(null);
        lesson.setVideoFileName(null);
        lesson.setVideoSizeBytes(null);
        lesson.setDurationMinutes(null);
        lesson.setDurationSeconds(null);
        return courses.changed(course);
    }

    @Transactional(readOnly = true)
    public CourseObjectStorage.Access materialAccess(Long courseId, Long lessonId, Long materialId) {
        courses.owned(courseId);
        CourseMaterial material = courses.material(courseId, lessonId, materialId);
        return storage.access(material.getFileUrl(), material.getFileName(), true);
    }

    public Builder updateVideoDuration(Long courseId, Long lessonId, Long version, Integer seconds) {
        Course course = courses.editable(courseId, version);
        Lesson lesson = courses.lesson(courseId, lessonId);
        if (lesson.getVideoUrl() == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No video attached");
        lesson.setDurationSeconds(seconds);
        lesson.setDurationMinutes(seconds / 60);
        return courses.changed(course);
    }

    @Transactional(readOnly = true)
    public CourseObjectStorage.Access videoAccess(Long courseId, Long lessonId, boolean download) {
        courses.owned(courseId);
        Lesson lesson = courses.lesson(courseId, lessonId);
        return access(lesson.getVideoUrl(), lesson.getVideoFileName(), download);
    }

    @Transactional(readOnly = true)
    public CourseObjectStorage.Access courseMediaAccess(Long courseId, boolean thumbnail) {
        Course course = courses.owned(courseId);
        return access(thumbnail ? course.getThumbnailUrl() : course.getIntroVideoUrl(), thumbnail ? "thumbnail" : "intro.mp4", false);
    }

    private CourseObjectStorage.Access access(String url, String name, boolean download) {
        if (url == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No file attached");
        return storage.access(url, name, download);
    }

    private CourseObjectStorage.Uploaded upload(Long courseId, Kind kind, MultipartFile file) {
        try (var validated = validator.validate(file, kind)) {
            return storage.upload(courseId, kind, validated.path(), validated.name(), validated.mimeType());
        } catch (IOException error) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot prepare upload");
        }
    }
}
