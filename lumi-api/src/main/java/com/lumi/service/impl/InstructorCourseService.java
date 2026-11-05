package com.lumi.service.impl;

import com.lumi.domain.*;
import com.lumi.domain.enumeration.*;
import com.lumi.repository.*;
import com.lumi.security.SecurityUtils;
import com.lumi.service.criteria.CourseCriteria;
import com.lumi.service.dto.InstructorCourseDTOs.*;
import com.lumi.service.mapper.*;
import com.lumi.service.query.CourseQueryService;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class InstructorCourseService {

    private final CourseRepository courses;
    private final ChapterRepository chapters;
    private final LessonRepository lessons;
    private final CourseMaterialRepository materials;
    private final UserRepository users;
    private final UserProfileRepository profiles;
    private final CourseReviewRequestRepository reviews;
    private final CourseMapper courseMapper;
    private final CourseQueryService courseQuery;
    private final ChapterMapper chapterMapper;
    private final LessonMapper lessonMapper;
    private final CourseMaterialMapper materialMapper;
    private final EntityManager em;
    private final com.lumi.service.storage.CourseStorageCleanup cleanup;

    public InstructorCourseService(
        CourseRepository courses,
        ChapterRepository chapters,
        LessonRepository lessons,
        CourseMaterialRepository materials,
        UserRepository users,
        UserProfileRepository profiles,
        CourseReviewRequestRepository reviews,
        CourseMapper courseMapper,
        CourseQueryService courseQuery,
        ChapterMapper chapterMapper,
        LessonMapper lessonMapper,
        CourseMaterialMapper materialMapper,
        EntityManager em,
        com.lumi.service.storage.CourseStorageCleanup cleanup
    ) {
        this.cleanup = cleanup;
        this.courses = courses;
        this.chapters = chapters;
        this.lessons = lessons;
        this.materials = materials;
        this.users = users;
        this.profiles = profiles;
        this.reviews = reviews;
        this.courseMapper = courseMapper;
        this.courseQuery = courseQuery;
        this.chapterMapper = chapterMapper;
        this.lessonMapper = lessonMapper;
        this.materialMapper = materialMapper;
        this.em = em;
    }

    private User currentInstructor() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new AccessDeniedException("Authentication required"));
        User user = users.findOneByLogin(login).orElseThrow(() -> new AccessDeniedException("Account not found"));
        boolean admin = SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN");
        boolean instructor = profiles
            .findOneByUserId(user.getId())
            .map(p -> p.getInstructorStatus() == InstructorApplicationStatus.ACTIVE)
            .orElse(false);
        if (!user.isActivated() || (!admin && !instructor)) throw new AccessDeniedException("Active instructor required");
        return user;
    }

    @Transactional(readOnly = true)
    public Course owned(Long id) {
        User user = currentInstructor();
        Course course = courses.findById(id).orElseThrow(() -> missing("Course"));
        if (Boolean.TRUE.equals(course.getIsDeleted()) || !course.getAuthor().getId().equals(user.getId())) throw missing("Course");
        return course;
    }

    // hàm ktra trc khi edit course
    public Course editable(Long id, Long version) {
        Course course = courses.findForBuilderUpdate(id).orElseThrow(() -> missing("Course"));
        User user = currentInstructor();
        if (Boolean.TRUE.equals(course.getIsDeleted()) || !course.getAuthor().getId().equals(user.getId())) throw missing("Course");
        if (
            course.getStatus() != RecordStatus.DRAFT || reviews.existsByCourseIdAndStatus(id, CourseReviewRequestStatus.PENDING)
        ) throw conflict("Course is not editable");
        if (version == null || !version.equals(course.getVersion())) throw conflict("Stale course version; reload before saving");
        return course;
    }

    public Builder create(CreateDraft input) {
        User author = currentInstructor();
        String title = input == null || input.title() == null || input.title().isBlank() ? "Khóa học chưa đặt tên" : input.title().trim();
        Course course = new Course()
            .title(title)
            .author(author)
            .status(RecordStatus.DRAFT)
            .isDeleted(false)
            .createdAt(Instant.now())
            .updatedAt(Instant.now());
        try {
            courses.saveAndFlush(course);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return builder(course);
    }

    @Transactional(readOnly = true)
    public Builder get(Long id) {
        return builder(owned(id));
    }

    @Transactional(readOnly = true)
    public Page<ManageCourse> list(CourseCriteria criteria, Pageable pageable) {
        User author = currentInstructor();
        Page<com.lumi.service.dto.CourseDTO> page = courseQuery.findForInstructor(criteria, pageable, author.getId());
        if (page.isEmpty()) return new PageImpl<>(List.of(), pageable, page.getTotalElements());
        Map<Long, CourseRepository.CurriculumStats> stats = courses
            .findCurriculumStats(page.getContent().stream().map(com.lumi.service.dto.CourseDTO::getId).toList())
            .stream()
            .collect(Collectors.toMap(CourseRepository.CurriculumStats::getCourseId, Function.identity()));
        return page.map(c -> {
            var s = stats.get(c.getId());
            long duration = s.getTotalDurationSeconds();
            return new ManageCourse(
                c.getId(),
                c.getVersion(),
                c.getTitle(),
                c.getSubject(),
                c.getGrade(),
                c.getBookSeries(),
                c.getStatus(),
                c.getPrice(),
                c.getAccessDurationValue(),
                c.getAccessDurationUnit(),
                c.getUpdatedAt(),
                c.getThumbnailUrl(),
                s.getChapterCount(),
                s.getLessonCount(),
                s.getVideoCount(),
                duration,
                duration / 3600.0
            );
        });
    }

    public void delete(Long id, Long version) {
        Course course = editable(id, version);
        // Keep curriculum and stored objects intact for references and possible recovery.
        course.setIsDeleted(true);
        course.setUpdatedAt(Instant.now());
        courses.saveAndFlush(course);
    }

    public Builder basicInfo(Long id, BasicInfo input) {
        Course course = editable(id, input.version());
        course.setTitle(input.title().trim());
        course.setSubject(input.subjectId() == null ? null : em.find(Subject.class, input.subjectId()));
        if (input.subjectId() != null && course.getSubject() == null) throw invalid("subjectId not found");
        course.setGrade(input.gradeId() == null ? null : em.find(Grade.class, input.gradeId()));
        if (input.gradeId() != null && course.getGrade() == null) throw invalid("gradeId not found");
        if (
            input.bookSeries() != null && !input.bookSeries().isBlank() && !"Kết nối tri thức với cuộc sống".equals(input.bookSeries())
        ) throw invalid("Unsupported book series");
        course.setBookSeries(input.bookSeries());
        course.setShortDescription(input.shortDescription());
        course.setDescription(input.description());
        course.setTargetAudience(input.targetAudience());
        course.setPrerequisites(input.prerequisites());
        course.setLearningObjectives(input.learningObjectives() == null ? null : new ArrayList<>(input.learningObjectives()));
        return changed(course);
    }

    public Builder pricing(Long id, Pricing input) {
        Course course = editable(id, input.version());
        course.setPrice(input.price());
        course.setAccessDurationValue(input.accessDurationValue());
        course.setAccessDurationUnit(input.accessDurationUnit());
        // Never approximate calendar months/years as a fixed number of days.
        course.setValidDurationDays(input.accessDurationUnit() == AccessDurationUnit.DAY ? input.accessDurationValue() : null);
        return changed(course);
    }

    public Builder submitReview(Long id, SubmitReview input) {
        Course course = editable(id, input.version());
        if (!Boolean.TRUE.equals(input.confirmedRights())) throw invalid("Content rights must be confirmed");
        Builder current = builder(course);
        if (!current.review().canSubmit()) throw invalid("Complete all required review checklist items before submitting");
        Instant now = Instant.now();
        course.setStatus(RecordStatus.PENDING);
        course.setUpdatedAt(now);
        courses.saveAndFlush(course);
        CourseReviewRequest request = new CourseReviewRequest();
        request.setCourse(course);
        request.setCourseVersion(course.getVersion());
        request.setStatus(CourseReviewRequestStatus.PENDING);
        request.setSubmissionNote(input.note() == null || input.note().isBlank() ? null : input.note().trim());
        request.setSubmittedBy(course.getAuthor());
        request.setSubmittedAt(now);
        request.setRightsConfirmedAt(now);
        reviews.saveAndFlush(request);
        return builder(course);
    }

    public Builder addChapter(Long id, ChapterInput input) {
        Course course = editable(id, input.version());
        List<Chapter> list = chapters.findByCourseIdOrderByOrderIndexAscIdAsc(id);
        chapters.save(new Chapter().course(course).title(input.title().trim()).orderIndex(nextChapterIndex(list)));
        return changed(course);
    }

    public Builder updateChapter(Long id, Long chapterId, ChapterInput input) {
        Course course = editable(id, input.version());
        chapter(id, chapterId).setTitle(input.title().trim());
        return changed(course);
    }

    public Builder deleteChapter(Long id, Long chapterId, Long version) {
        Course course = editable(id, version);
        Chapter chapter = chapter(id, chapterId);
        for (Lesson lesson : lessons.findByChapterIdOrderByOrderIndexAscIdAsc(chapterId)) removeLesson(lesson);
        chapters.delete(chapter);
        int index = 0;
        for (Chapter remaining : chapters.findByCourseIdOrderByOrderIndexAscIdAsc(id)) remaining.setOrderIndex(index++);
        return changed(course);
    }

    public Builder addLesson(Long id, Long chapterId, LessonInput input) {
        Course course = editable(id, input.version());
        Chapter chapter = chapter(id, chapterId);
        int index =
            lessons.findByChapterIdOrderByOrderIndexAscIdAsc(chapterId).stream().mapToInt(Lesson::getOrderIndex).max().orElse(-1) + 1;
        lessons.save(
            new Lesson()
                .chapter(chapter)
                .title(input.title().trim())
                .description(input.description())
                .isPreview(input.isPreview())
                .orderIndex(index)
                .contentType("DOCUMENT")
                .status(RecordStatus.DRAFT)
                .updatedAt(Instant.now())
        );
        return changed(course);
    }

    public Builder updateLesson(Long id, Long lessonId, LessonInput input) {
        Course course = editable(id, input.version());
        Lesson lesson = lesson(id, lessonId);
        lesson.setTitle(input.title().trim());
        lesson.setDescription(input.description());
        lesson.setIsPreview(input.isPreview());
        lesson.setUpdatedAt(Instant.now());
        return changed(course);
    }

    public Builder deleteLesson(Long id, Long lessonId, Long version) {
        Course course = editable(id, version);
        Lesson lesson = lesson(id, lessonId);
        Long chapterId = lesson.getChapter().getId();
        removeLesson(lesson);
        int index = 0;
        for (Lesson remaining : lessons.findByChapterIdOrderByOrderIndexAscIdAsc(chapterId)) remaining.setOrderIndex(index++);
        return changed(course);
    }

    private void removeLesson(Lesson lesson) {
        for (String entity : List.of("StudentNote", "AiConversation", "Discussion")) {
            Long count = em
                .createQuery("select count(x) from " + entity + " x where x.lesson.id = :id", Long.class)
                .setParameter("id", lesson.getId())
                .getSingleResult();
            if (count > 0) throw conflict("Lesson has learning activity and cannot be deleted");
        }
        for (CourseMaterial material : materials.findByLessonIdOrderByOrderIndexAscIdAsc(lesson.getId())) removeMaterial(material);
        cleanup.afterCommit(lesson.getVideoUrl());
        lessons.delete(lesson);
    }

    private void removeMaterial(CourseMaterial material) {
        em.createQuery("delete from PdfChunk p where p.material.id = :id").setParameter("id", material.getId()).executeUpdate();
        cleanup.afterCommit(material.getFileUrl());
        materials.delete(material);
    }

    public Builder reorder(Long id, CurriculumOrder input) {
        Course course = editable(id, input.version());
        Map<Long, Chapter> chapterMap = chapters
            .findByCourseIdOrderByOrderIndexAscIdAsc(id)
            .stream()
            .collect(Collectors.toMap(Chapter::getId, Function.identity()));
        Map<Long, Lesson> lessonMap = lessons
            .findByChapterCourseIdOrderByOrderIndexAscIdAsc(id)
            .stream()
            .collect(Collectors.toMap(Lesson::getId, Function.identity()));
        List<Long> chapterIds = input.chapters().stream().map(ChapterOrder::id).toList();
        List<Long> lessonIds = input
            .chapters()
            .stream()
            .flatMap(c -> c.lessonIds().stream())
            .toList();
        exactIds(chapterIds, chapterMap.keySet());
        exactIds(lessonIds, lessonMap.keySet());
        for (int i = 0; i < input.chapters().size(); i++) {
            ChapterOrder node = input.chapters().get(i);
            Chapter chapter = chapterMap.get(node.id());
            chapter.setOrderIndex(i);
            for (int j = 0; j < node.lessonIds().size(); j++) {
                Lesson lesson = lessonMap.get(node.lessonIds().get(j));
                lesson.setChapter(chapter);
                lesson.setOrderIndex(j);
            }
        }
        return changed(course);
    }

    public Builder renameMaterial(Long id, Long lessonId, Long materialId, MaterialInput input) {
        Course course = editable(id, input.version());
        material(id, lessonId, materialId).setDisplayName(input.displayName());
        return changed(course);
    }

    public Builder moveLesson(Long courseId, Long lessonId, MoveLesson input) {
        Course course = editable(courseId, input.version());
        Lesson moving = lesson(courseId, lessonId);
        Chapter target = chapter(courseId, input.chapterId());
        Long sourceId = moving.getChapter().getId();
        if (sourceId.equals(target.getId())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose another chapter");
        List<Lesson> targetLessons = lessons.findByChapterIdOrderByOrderIndexAscIdAsc(target.getId());
        Set<String> titles = targetLessons
            .stream()
            .map(l -> l.getTitle().trim().toLowerCase(Locale.ROOT))
            .collect(Collectors.toSet());
        String title = uniqueLessonTitle(moving.getTitle().trim(), titles);
        // FE hiển thị tên này trước khi xác nhận. Nếu khác, không đổi tên âm thầm.
        if (!title.equals(input.title())) throw conflict("Lesson title changed; reload and confirm the new title");
        List<Lesson> sourceLessons = lessons.findByChapterIdOrderByOrderIndexAscIdAsc(sourceId);
        moving.setTitle(title);
        moving.setChapter(target);
        moving.setOrderIndex(targetLessons.stream().mapToInt(Lesson::getOrderIndex).max().orElse(-1) + 1);
        int index = 0;
        for (Lesson remaining : sourceLessons) {
            if (!remaining.getId().equals(lessonId)) remaining.setOrderIndex(index++);
        }
        return changed(course);
    }

    private String uniqueLessonTitle(String title, Set<String> existing) {
        if (!existing.contains(title.toLowerCase(Locale.ROOT))) return title;
        var match = java.util.regex.Pattern.compile("^(.*?)\\s*\\((\\d+)\\)$").matcher(title);
        String root = title;
        java.math.BigInteger index = java.math.BigInteger.ONE;
        if (match.matches()) {
            root = match.group(1).trim();
            index = new java.math.BigInteger(match.group(2)).add(java.math.BigInteger.ONE);
        }
        while (true) {
            String suffix = " (" + index + ")";
            String candidate = root.substring(0, Math.min(root.length(), 255 - suffix.length())).stripTrailing() + suffix;
            if (!existing.contains(candidate.toLowerCase(Locale.ROOT))) return candidate;
            index = index.add(java.math.BigInteger.ONE);
        }
    }

    public Builder reorderMaterials(Long id, Long lessonId, MaterialOrder input) {
        Course course = editable(id, input.version());
        lesson(id, lessonId);
        Map<Long, CourseMaterial> map = materials
            .findByLessonIdOrderByOrderIndexAscIdAsc(lessonId)
            .stream()
            .collect(Collectors.toMap(CourseMaterial::getId, Function.identity()));
        exactIds(input.materialIds(), map.keySet());
        for (int i = 0; i < input.materialIds().size(); i++) map.get(input.materialIds().get(i)).setOrderIndex(i);
        return changed(course);
    }

    public Builder deleteMaterial(Long id, Long lessonId, Long materialId, Long version) {
        Course course = editable(id, version);
        removeMaterial(material(id, lessonId, materialId));
        int index = 0;
        for (CourseMaterial material : materials.findByLessonIdOrderByOrderIndexAscIdAsc(lessonId)) material.setOrderIndex(index++);
        return changed(course);
    }

    public Chapter chapter(Long courseId, Long chapterId) {
        Chapter chapter = chapters.findById(chapterId).orElseThrow(() -> missing("Chapter"));
        if (!chapter.getCourse().getId().equals(courseId)) throw missing("Chapter");
        return chapter;
    }

    public Lesson lesson(Long courseId, Long lessonId) {
        Lesson lesson = lessons.findById(lessonId).orElseThrow(() -> missing("Lesson"));
        if (!lesson.getChapter().getCourse().getId().equals(courseId)) throw missing("Lesson");
        return lesson;
    }

    public CourseMaterial material(Long courseId, Long lessonId, Long materialId) {
        lesson(courseId, lessonId);
        CourseMaterial material = materials.findById(materialId).orElseThrow(() -> missing("Material"));
        if (!material.getLesson().getId().equals(lessonId)) throw missing("Material");
        return material;
    }

    public Builder changed(Course course) {
        course.setUpdatedAt(Instant.now());
        courses.saveAndFlush(course);
        return builder(course);
    }

    private Builder builder(Course course) {
        List<Chapter> chapterList = chapters.findByCourseIdOrderByOrderIndexAscIdAsc(course.getId());
        List<Lesson> lessonList = lessons.findByChapterCourseIdOrderByOrderIndexAscIdAsc(course.getId());
        List<CourseMaterial> materialList = materials.findByLessonChapterCourseIdOrderByOrderIndexAscIdAsc(course.getId());
        List<ChapterNode> nodes = chapterList
            .stream()
            .map(chapter ->
                new ChapterNode(
                    chapterMapper.toDto(chapter),
                    lessonList
                        .stream()
                        .filter(l -> l.getChapter().getId().equals(chapter.getId()))
                        .map(lesson ->
                            new LessonNode(
                                lessonMapper.toDto(lesson),
                                materialList
                                    .stream()
                                    .filter(m -> m.getLesson().getId().equals(lesson.getId()))
                                    .map(materialMapper::toDto)
                                    .toList()
                            )
                        )
                        .toList()
                )
            )
            .toList();
        var courseDto = courseMapper.toDto(course);
        var latest = reviews.findFirstByCourseIdOrderBySubmittedAtDescIdDesc(course.getId()).orElse(null);
        return new Builder(
            courseDto,
            nodes,
            lessonList.size(),
            materialList.size(),
            (int) lessonList
                .stream()
                .filter(l -> l.getVideoUrl() != null)
                .count(),
            CourseReviewChecklist.evaluate(courseDto, nodes, latest)
        );
    }

    private int nextChapterIndex(List<Chapter> list) {
        return list.stream().mapToInt(Chapter::getOrderIndex).max().orElse(-1) + 1;
    }

    private void exactIds(List<Long> ids, Set<Long> expected) {
        if (ids.size() != expected.size() || new HashSet<>(ids).size() != ids.size() || !new HashSet<>(ids).equals(expected)) throw invalid(
            "IDs must include every current item exactly once, from this course only"
        );
    }

    private ResponseStatusException missing(String entity) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, entity + " not found");
    }

    private ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }

    private ResponseStatusException invalid(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
