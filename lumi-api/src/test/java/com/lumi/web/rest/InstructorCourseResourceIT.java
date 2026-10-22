package com.lumi.web.rest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.*;
import com.lumi.domain.enumeration.*;
import com.lumi.repository.*;
import com.lumi.service.InstructorCourseService;
import com.lumi.service.dto.InstructorCourseDTOs.*;
import com.lumi.service.storage.*;
import jakarta.persistence.EntityManager;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "builder-teacher", authorities = "ROLE_USER")
class InstructorCourseResourceIT {

    @Autowired
    MockMvc mvc;

    @Autowired
    CourseStorageCleanup cleanup;

    @Autowired
    ObjectMapper json;

    @Autowired
    EntityManager em;

    @Autowired
    InstructorCourseService service;

    @Autowired
    CourseRepository courses;

    @Autowired
    CourseMaterialRepository materials;

    @MockitoBean
    CourseObjectStorage storage;

    private User teacher;

    @BeforeEach
    void setup() throws Exception {
        reset(storage);
        teacher = new User();
        teacher.setLogin("builder-teacher");
        teacher.setPassword("x".repeat(60));
        teacher.setActivated(true);
        em.persist(teacher);
        em.persist(new UserProfile().user(teacher).instructorStatus(InstructorApplicationStatus.ACTIVE));
        em.flush();
        when(storage.upload(anyLong(), any(), any(), anyString(), anyString())).thenAnswer(call ->
            new CourseObjectStorage.Uploaded(
                "https://cloud.example/" + UUID.randomUUID(),
                call.getArgument(3),
                call.getArgument(4),
                Files.size(call.getArgument(2))
            )
        );
        when(storage.access(anyString(), any(), anyBoolean())).thenAnswer(call ->
            new CourseObjectStorage.Access("https://signed.example/file", Instant.now().plusSeconds(300))
        );
    }

    private void asTeacher() {
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(
            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                "builder-teacher",
                "n/a",
                org.springframework.security.core.authority.AuthorityUtils.createAuthorityList("ROLE_USER")
            )
        );
    }

    @org.springframework.test.context.transaction.AfterTransaction
    void awaitObjectCleanup() throws Exception {
        cleanup.completion().toCompletableFuture().get(30, java.util.concurrent.TimeUnit.SECONDS);
    }

    private Builder create() throws Exception {
        var result = mvc
            .perform(post("/api/instructor/courses").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"My draft\"}"))
            .andExpect(status().isCreated())
            .andReturn();
        asTeacher();
        return json.readValue(result.getResponse().getContentAsString(), Builder.class);
    }

    private Builder chapter(Builder builder, String title) {
        return service.addChapter(builder.course().getId(), new ChapterInput(builder.course().getVersion(), title));
    }

    private Builder lesson(Builder builder, Long chapterId, String title) {
        return service.addLesson(
            builder.course().getId(),
            chapterId,
            new LessonInput(builder.course().getVersion(), title, "Read the documents", false)
        );
    }

    private Builder uploadPdf(Builder builder, Long lessonId, Long materialId) throws Exception {
        String path = "/api/instructor/courses/" + builder.course().getId() + "/lessons/" + lessonId + "/materials";
        if (materialId != null) path += "/" + materialId + "/file";
        var request = multipart(path)
            .file(new MockMultipartFile("file", "lesson.pdf", "application/pdf", "%PDF-1.7\n%%EOF".getBytes()))
            .param("version", builder.course().getVersion().toString());
        if (materialId != null) request.with(req -> {
            req.setMethod("PUT");
            return req;
        });
        var response = mvc
            .perform(request)
            .andExpect(materialId == null ? status().isCreated() : status().isOk())
            .andReturn();
        asTeacher();
        return json.readValue(response.getResponse().getContentAsString(), Builder.class);
    }

    @Test
    void pricingRoundTripFreePaidAndCalendarUnits() throws Exception {
        Builder draft = create();
        String path = "/api/instructor/courses/" + draft.course().getId() + "/pricing";
        for (AccessDurationUnit unit : AccessDurationUnit.values()) {
            Long previousVersion = draft.course().getVersion();
            var input = new Pricing(
                previousVersion,
                unit == AccessDurationUnit.DAY ? java.math.BigDecimal.ZERO : new java.math.BigDecimal("499000"),
                12,
                unit
            );
            var response = mvc
                .perform(put(path).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(input)))
                .andExpect(status().isOk())
                .andReturn();
            draft = json.readValue(response.getResponse().getContentAsString(), Builder.class);
            assertThat(draft.course().getVersion()).isGreaterThan(previousVersion);
            em.clear();
            asTeacher();
            Builder loaded = service.get(draft.course().getId());
            assertThat(loaded.course().getPrice()).isEqualByComparingTo(input.price());
            assertThat(loaded.course().getAccessDurationValue()).isEqualTo(12);
            assertThat(loaded.course().getAccessDurationUnit()).isEqualTo(unit);
            assertThat(loaded.course().getValidDurationDays()).isEqualTo(unit == AccessDurationUnit.DAY ? 12 : null);
            mvc.perform(put(path).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(input))).andExpect(
                status().isConflict()
            );
            asTeacher();
        }
    }

    @Test
    void pricingRejectsInvalidInputWithoutChangingDraft() throws Exception {
        Builder draft = create();
        String path = "/api/instructor/courses/" + draft.course().getId() + "/pricing";
        for (String fields : List.of(
            "\"price\":-1,\"accessDurationValue\":1,\"accessDurationUnit\":\"DAY\"",
            "\"price\":1.5,\"accessDurationValue\":1,\"accessDurationUnit\":\"DAY\"",
            "\"price\":10000000000000000000,\"accessDurationValue\":1,\"accessDurationUnit\":\"DAY\"",
            "\"price\":0,\"accessDurationValue\":0,\"accessDurationUnit\":\"DAY\"",
            "\"price\":0,\"accessDurationValue\":-1,\"accessDurationUnit\":\"DAY\"",
            "\"price\":0,\"accessDurationValue\":1,\"accessDurationUnit\":\"WEEK\"",
            "\"price\":null,\"accessDurationValue\":1,\"accessDurationUnit\":\"DAY\"",
            "\"price\":0,\"accessDurationUnit\":\"DAY\"",
            "\"price\":0,\"accessDurationValue\":1"
        )) {
            mvc.perform(
                put(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"version\":" + draft.course().getVersion() + "," + fields + "}")
            ).andExpect(status().isBadRequest());
        }
        em.clear();
        assertThat(courses.findById(draft.course().getId()).orElseThrow().getPrice()).isNull();
    }

    @Test
    void pricingCannotEditForeignOrLockedCourse() throws Exception {
        Builder draft = create();
        Course course = courses.findById(draft.course().getId()).orElseThrow();
        course.setStatus(RecordStatus.PENDING);
        em.flush();
        String path = "/api/instructor/courses/" + course.getId() + "/pricing";
        var input = new Pricing(course.getVersion(), java.math.BigDecimal.ZERO, 1, AccessDurationUnit.YEAR);
        mvc.perform(put(path).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(input))).andExpect(
            status().isConflict()
        );
        asTeacher();
        course.setStatus(RecordStatus.DRAFT);
        User other = new User();
        other.setLogin("pricing-other");
        other.setPassword("x".repeat(60));
        other.setActivated(true);
        em.persist(other);
        course.setAuthor(other);
        em.flush();
        mvc.perform(put(path).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(input))).andExpect(
            status().isNotFound()
        );
    }

    @Test
    void pricingCannotEditPendingReviewOrDeletedCourse() throws Exception {
        Builder draft = create();
        Course course = courses.findById(draft.course().getId()).orElseThrow();
        String path = "/api/instructor/courses/" + course.getId() + "/pricing";
        var review = new CourseReviewRequest();
        review.setCourse(course);
        review.setCourseVersion(course.getVersion());
        review.setStatus(CourseReviewRequestStatus.PENDING);
        review.setSubmittedBy(teacher);
        review.setSubmittedAt(Instant.now());
        review.setRightsConfirmedAt(Instant.now());
        em.persist(review);
        em.flush();
        var input = new Pricing(course.getVersion(), java.math.BigDecimal.ZERO, 1, AccessDurationUnit.DAY);
        mvc.perform(put(path).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(input))).andExpect(
            status().isConflict()
        );
        asTeacher();
        course.setIsDeleted(true);
        em.flush();
        mvc.perform(put(path).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(input))).andExpect(
            status().isNotFound()
        );
        assertThat(course.getPrice()).isNull();
    }

    @Test
    void pricingRequiresApprovedInstructor() throws Exception {
        Builder draft = create();
        User student = new User();
        student.setLogin("pricing-student");
        student.setActivated(true);
        student.setPassword("x".repeat(60));
        em.persist(student);
        em.flush();
        var input = new Pricing(draft.course().getVersion(), java.math.BigDecimal.ZERO, 1, AccessDurationUnit.DAY);
        mvc.perform(
            put("/api/instructor/courses/" + draft.course().getId() + "/pricing")
                .with(
                    org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(
                        "pricing-student"
                    ).roles("USER")
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(input))
        ).andExpect(status().isForbidden());
        assertThat(courses.findById(draft.course().getId()).orElseThrow().getPrice()).isNull();
    }

    @Test
    void reviewChecklistRejectsIncompleteDraftAndUnconfirmedRights() throws Exception {
        Builder draft = create();
        assertThat(draft.review().canSubmit()).isFalse();
        assertThat(draft.review().checks()).anyMatch(item -> item.required() && !item.complete());
        String path = "/api/instructor/courses/" + draft.course().getId();
        mvc.perform(get(path + "/review"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.canSubmit").value(false));
        for (Boolean rights : Arrays.asList(false, null, true)) {
            mvc.perform(
                post(path + "/review-requests")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.writeValueAsBytes(new SubmitReview(draft.course().getVersion(), null, rights)))
            ).andExpect(status().isBadRequest());
            asTeacher();
        }
        assertThat(courses.findById(draft.course().getId()).orElseThrow().getStatus()).isEqualTo(RecordStatus.DRAFT);
        assertThat(
            em
                .createQuery("select count(r) from CourseReviewRequest r where r.course.id = :id", Long.class)
                .setParameter("id", draft.course().getId())
                .getSingleResult()
        ).isZero();
    }

    private Builder readyForReview() throws Exception {
        Builder draft = chapter(create(), "Review chapter");
        draft = lesson(draft, draft.chapters().getFirst().chapter().getId(), "Document lesson");
        Course course = courses.findById(draft.course().getId()).orElseThrow();
        Subject subject = new Subject().name("Review subject");
        Grade grade = new Grade().level(9).name("Grade 9");
        em.persist(subject);
        em.persist(grade);
        course.setTitle("Ready for review");
        course.setSubject(subject);
        course.setGrade(grade);
        course.setShortDescription("Summary");
        course.setDescription("Full description");
        course.setTargetAudience("Students");
        course.setLearningObjectives(List.of("Read and practice"));
        course.setThumbnailUrl("private/cover.png");
        course.setPrice(java.math.BigDecimal.ZERO);
        course.setAccessDurationValue(1);
        course.setAccessDurationUnit(AccessDurationUnit.MONTH);
        Lesson lesson = em.find(Lesson.class, draft.chapters().getFirst().lessons().getFirst().lesson().getId());
        em.persist(new CourseMaterial().lesson(lesson).fileType("PDF").fileUrl("private/lesson.pdf").fileName("lesson.pdf"));
        em.flush();
        return service.get(course.getId());
    }

    @Test
    void reviewSubmissionPersistsRequestLocksDraftAndRejectsDuplicate() throws Exception {
        Builder draft = readyForReview();
        assertThat(draft.review().canSubmit()).isTrue();
        assertThat(draft.review().checks()).anyMatch(item -> !item.required() && !item.complete());
        String path = "/api/instructor/courses/" + draft.course().getId();
        var input = new SubmitReview(draft.course().getVersion(), " Please review documents ", true);
        var response = mvc
            .perform(
                post(path + "/review-requests")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.writeValueAsBytes(input))
            )
            .andExpect(status().isCreated())
            .andReturn();
        Builder submitted = json.readValue(response.getResponse().getContentAsString(), Builder.class);
        assertThat(submitted.course().getStatus()).isEqualTo(RecordStatus.PENDING);
        assertThat(submitted.course().getVersion()).isGreaterThan(draft.course().getVersion());
        assertThat(submitted.review().canSubmit()).isFalse();
        assertThat(submitted.review().request().status()).isEqualTo(CourseReviewRequestStatus.PENDING);
        assertThat(submitted.review().request().courseVersion()).isEqualTo(submitted.course().getVersion());
        assertThat(submitted.review().request().submissionNote()).isEqualTo("Please review documents");
        assertThat(submitted.review().request().rightsConfirmedAt()).isEqualTo(submitted.review().request().submittedAt());
        var stored = em.find(CourseReviewRequest.class, submitted.review().request().id());
        assertThat(stored.getSubmittedBy().getId()).isEqualTo(teacher.getId());
        em.clear();
        asTeacher();
        var loadedResponse = mvc
            .perform(get(path + "/builder"))
            .andExpect(status().isOk())
            .andReturn();
        Builder loaded = json.readValue(loadedResponse.getResponse().getContentAsString(), Builder.class);
        assertThat(loaded.review().request().id()).isEqualTo(submitted.review().request().id());
        for (Long version : List.of(input.version(), submitted.course().getVersion())) {
            mvc.perform(
                post(path + "/review-requests")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.writeValueAsBytes(new SubmitReview(version, "Duplicate", true)))
            ).andExpect(status().isConflict());
            asTeacher();
        }
        mvc.perform(
            put(path + "/pricing")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    json.writeValueAsBytes(
                        new Pricing(submitted.course().getVersion(), java.math.BigDecimal.TEN, 1, AccessDurationUnit.DAY)
                    )
                )
        ).andExpect(status().isConflict());
        asTeacher();
        mvc.perform(
            post(path + "/chapters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new ChapterInput(submitted.course().getVersion(), "Cannot edit")))
        ).andExpect(status().isConflict());
        assertThat(
            em
                .createQuery("select count(r) from CourseReviewRequest r where r.course.id = :id", Long.class)
                .setParameter("id", draft.course().getId())
                .getSingleResult()
        ).isEqualTo(1L);
    }

    @Test
    void reviewRejectsStaleVersionForeignCourseAndOversizedNote() throws Exception {
        Builder draft = readyForReview();
        String path = "/api/instructor/courses/" + draft.course().getId() + "/review-requests";
        mvc.perform(
            post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new SubmitReview(draft.course().getVersion(), "x".repeat(5001), true)))
        ).andExpect(status().isBadRequest());
        asTeacher();
        service.pricing(
            draft.course().getId(),
            new Pricing(draft.course().getVersion(), java.math.BigDecimal.TEN, 1, AccessDurationUnit.DAY)
        );
        mvc.perform(
            post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new SubmitReview(draft.course().getVersion(), "Old tab", true)))
        ).andExpect(status().isConflict());
        asTeacher();
        Course course = courses.findById(draft.course().getId()).orElseThrow();
        User other = new User();
        other.setLogin("review-other");
        other.setPassword("x".repeat(60));
        other.setActivated(true);
        em.persist(other);
        course.setAuthor(other);
        em.flush();
        mvc.perform(
            post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new SubmitReview(course.getVersion(), null, true)))
        ).andExpect(status().isNotFound());
        mvc.perform(
            post(path)
                .with(
                    org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(
                        "unverified-reviewer"
                    ).roles("USER")
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new SubmitReview(course.getVersion(), null, true)))
        ).andExpect(status().isForbidden());
    }

    @Test
    void draftBasicInfoRoundTripAndMassAssignmentProtection() throws Exception {
        Builder draft = create();
        Long id = draft.course().getId();
        assertThat(draft.course().getAuthor().getId()).isEqualTo(teacher.getId());
        assertThat(draft.course().getStatus()).isEqualTo(RecordStatus.DRAFT);
        assertThat(draft.course().getValidDurationDays()).isNull();
        Subject subject = new Subject().name("Builder Math");
        em.persist(subject);
        Grade grade = new Grade().level(6).name("Grade 6");
        em.persist(grade);
        em.flush();
        var input = new BasicInfo(
            draft.course().getVersion(),
            "Toán 6",
            subject.getId(),
            grade.getId(),
            "Kết nối tri thức với cuộc sống",
            "Summary",
            "Full",
            "Students",
            null,
            List.of("Đọc PDF", "Đọc DOCX")
        );
        var response = mvc
            .perform(
                put("/api/instructor/courses/" + id + "/basic-info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.writeValueAsBytes(input))
            )
            .andExpect(status().isOk())
            .andReturn();
        Builder saved = json.readValue(response.getResponse().getContentAsString(), Builder.class);
        assertThat(saved.course().getVersion()).isGreaterThan(draft.course().getVersion());
        em.clear();
        var fetched = mvc
            .perform(get("/api/instructor/courses/" + id + "/builder"))
            .andExpect(status().isOk())
            .andReturn();
        Builder loaded = json.readValue(fetched.getResponse().getContentAsString(), Builder.class);
        assertThat(loaded.course().getLearningObjectives()).containsExactly("Đọc PDF", "Đọc DOCX");
        assertThat(loaded.course().getSubject().getId()).isEqualTo(subject.getId());
        mvc.perform(
            put("/api/instructor/courses/" + id + "/basic-info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(input))
        ).andExpect(status().isConflict());
        assertThat(courses.findById(id).orElseThrow().getTitle()).isEqualTo("Toán 6");
    }

    @Test
    void chapterLessonCrudAndMovePersistInOrder() throws Exception {
        Builder draft = chapter(create(), "First");
        Long id = draft.course().getId();
        Long first = draft.chapters().getFirst().chapter().getId();
        draft = chapter(draft, "Second");
        Long second = draft.chapters().get(1).chapter().getId();
        draft = lesson(draft, first, "Document lesson");
        Long lesson = draft.chapters().getFirst().lessons().getFirst().lesson().getId();
        var input = new CurriculumOrder(
            draft.course().getVersion(),
            List.of(new ChapterOrder(second, List.of(lesson)), new ChapterOrder(first, List.of()))
        );
        var response = mvc
            .perform(
                put("/api/instructor/courses/" + id + "/curriculum/order")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.writeValueAsBytes(input))
            )
            .andExpect(status().isOk())
            .andReturn();
        Builder moved = json.readValue(response.getResponse().getContentAsString(), Builder.class);
        assertThat(moved.chapters().getFirst().chapter().getId()).isEqualTo(second);
        assertThat(moved.chapters().getFirst().lessons().getFirst().lesson().getId()).isEqualTo(lesson);
        assertThat(moved.chapters().getFirst().lessons().getFirst().lesson().getVideoUrl()).isNull();
        assertThat(moved.totalVideos()).isZero();
        em.clear();
        asTeacher();
        Builder loaded = service.get(id);
        assertThat(loaded.chapters().getFirst().lessons().getFirst().lesson().getChapter().getId()).isEqualTo(second);
        Builder renamed = service.updateLesson(
            id,
            lesson,
            new LessonInput(loaded.course().getVersion(), "Renamed", "New description", true)
        );
        assertThat(renamed.chapters().getFirst().lessons().getFirst().lesson().getIsPreview()).isTrue();
        Builder deleted = service.deleteChapter(id, second, renamed.course().getVersion());
        assertThat(deleted.totalLessons()).isZero();
        assertThat(deleted.chapters()).hasSize(1);
        assertThat(deleted.chapters().getFirst().chapter().getOrderIndex()).isZero();
    }

    @Test
    void foreignIdsDuplicatesAndOmissionsCannotCorruptCurriculum() throws Exception {
        Builder a = chapter(create(), "A");
        Long id = a.course().getId();
        Long chapter = a.chapters().getFirst().chapter().getId();
        a = lesson(a, chapter, "A lesson");
        Long lessonId = a.chapters().getFirst().lessons().getFirst().lesson().getId();
        Builder b = chapter(create(), "B");
        Long foreign = b.chapters().getFirst().chapter().getId();
        for (List<ChapterOrder> invalid : List.of(
            List.of(new ChapterOrder(foreign, List.of(lessonId))),
            List.of(new ChapterOrder(chapter, List.of(lessonId, lessonId))),
            List.of(new ChapterOrder(chapter, List.of()))
        )) {
            mvc.perform(
                put("/api/instructor/courses/" + id + "/curriculum/order")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.writeValueAsBytes(new CurriculumOrder(a.course().getVersion(), invalid)))
            ).andExpect(status().isBadRequest());
        }
        mvc.perform(
            put("/api/instructor/courses/" + id + "/chapters/" + foreign)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new ChapterInput(a.course().getVersion(), "Stolen")))
        ).andExpect(status().isNotFound());
        asTeacher();
        assertThat(service.get(id).totalLessons()).isEqualTo(1);
    }

    @Test
    void nonOwnerCannotReadMutateOrDownloadAndLegacyCrudIsBlocked() throws Exception {
        User other = new User();
        other.setLogin("other-builder");
        other.setActivated(true);
        other.setPassword("x".repeat(60));
        em.persist(other);
        Course course = new Course().title("Private").author(other).status(RecordStatus.DRAFT).isDeleted(false);
        em.persist(course);
        em.flush();
        Long id = course.getId();
        mvc.perform(get("/api/instructor/courses/" + id + "/builder")).andExpect(status().isNotFound());
        mvc.perform(
            post("/api/instructor/courses/" + id + "/chapters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new ChapterInput(course.getVersion(), "Illegal")))
        ).andExpect(status().isNotFound());
        mvc.perform(get("/api/instructor/courses/" + id + "/lessons/1/video/playback")).andExpect(status().isNotFound());
        for (String endpoint : List.of("courses", "chapters", "lessons", "course-materials", "user-profiles", "teacher-verifications"))
            mvc.perform(get("/api/" + endpoint)).andExpect(status().isForbidden());
        verifyNoInteractions(storage);
    }

    @Test
    @WithMockUser(username = "ordinary-student", authorities = "ROLE_USER")
    void unverifiedAccountCannotCreateCourse() throws Exception {
        User student = new User();
        student.setLogin("ordinary-student");
        student.setActivated(true);
        student.setPassword("x".repeat(60));
        em.persist(student);
        em.flush();
        mvc.perform(post("/api/instructor/courses").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(
            status().isForbidden()
        );
    }

    @Test
    void pendingCourseAndPendingReviewAreLocked() throws Exception {
        Builder draft = create();
        Course course = courses.findById(draft.course().getId()).orElseThrow();
        course.setStatus(RecordStatus.PENDING);
        em.flush();
        mvc.perform(
            post("/api/instructor/courses/" + course.getId() + "/chapters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new ChapterInput(course.getVersion(), "Illegal")))
        ).andExpect(status().isConflict());
    }

    @Test
    void documentUploadReplaceRenameReorderAndDelete() throws Exception {
        Builder draft = chapter(create(), "Chapter");
        Long chapterId = draft.chapters().getFirst().chapter().getId();
        draft = lesson(draft, chapterId, "Documents");
        Long lessonId = draft.chapters().getFirst().lessons().getFirst().lesson().getId();
        draft = uploadPdf(draft, lessonId, null);
        var first = draft.chapters().getFirst().lessons().getFirst().materials().getFirst();
        assertThat(first.getFileType()).isEqualTo("PDF");
        assertThat(first.getFileName()).isEqualTo("lesson.pdf");
        assertThat(draft.totalMaterials()).isEqualTo(1);
        assertThat(draft.totalVideos()).isZero();
        String old = first.getFileUrl();
        draft = uploadPdf(draft, lessonId, first.getId());
        assertThat(draft.chapters().getFirst().lessons().getFirst().materials().getFirst().getFileUrl()).isNotEqualTo(old);
        draft = uploadPdf(draft, lessonId, null);
        Long second = draft.chapters().getFirst().lessons().getFirst().materials().get(1).getId();
        draft = service.renameMaterial(
            draft.course().getId(),
            lessonId,
            second,
            new MaterialInput(draft.course().getVersion(), "Second document")
        );
        draft = service.reorderMaterials(
            draft.course().getId(),
            lessonId,
            new MaterialOrder(draft.course().getVersion(), List.of(second, first.getId()))
        );
        assertThat(draft.chapters().getFirst().lessons().getFirst().materials().getFirst().getDisplayName()).isEqualTo("Second document");
        mvc.perform(
            get("/api/instructor/courses/" + draft.course().getId() + "/lessons/" + lessonId + "/materials/" + second + "/download")
        )
            .andExpect(status().isOk())
            .andExpect(header().string("Cache-Control", "no-store"));
        asTeacher();
        draft = service.deleteMaterial(draft.course().getId(), lessonId, second, draft.course().getVersion());
        assertThat(draft.totalMaterials()).isEqualTo(1);
    }

    @Test
    void failedReplacementAndInvalidFileRetainExistingMaterial() throws Exception {
        Builder draft = chapter(create(), "Chapter");
        draft = lesson(draft, draft.chapters().getFirst().chapter().getId(), "Documents");
        Long lessonId = draft.chapters().getFirst().lessons().getFirst().lesson().getId();
        draft = uploadPdf(draft, lessonId, null);
        var material = draft.chapters().getFirst().lessons().getFirst().materials().getFirst();
        String path =
            "/api/instructor/courses/" + draft.course().getId() + "/lessons/" + lessonId + "/materials/" + material.getId() + "/file";
        mvc.perform(
            multipart(path)
                .file(new MockMultipartFile("file", "fake.pdf", "application/pdf", "not a PDF".getBytes()))
                .param("version", draft.course().getVersion().toString())
                .with(r -> {
                    r.setMethod("PUT");
                    return r;
                })
        ).andExpect(status().isBadRequest());
        doThrow(new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Simulated upload failure"))
            .when(storage)
            .upload(anyLong(), any(), any(), anyString(), anyString());
        mvc.perform(
            multipart(path)
                .file(new MockMultipartFile("file", "new.pdf", "application/pdf", "%PDF-1.7\n%%EOF".getBytes()))
                .param("version", draft.course().getVersion().toString())
                .with(r -> {
                    r.setMethod("PUT");
                    return r;
                })
        ).andExpect(status().isBadGateway());
        assertThat(materials.findById(material.getId()).orElseThrow().getFileUrl()).isEqualTo(material.getFileUrl());
    }

    private Builder uploadMedia(Builder draft, String suffix, MockMultipartFile file) throws Exception {
        var response = mvc
            .perform(
                multipart("/api/instructor/courses/" + draft.course().getId() + suffix)
                    .file(file)
                    .param("version", draft.course().getVersion().toString())
            )
            .andExpect(status().isOk())
            .andReturn();
        asTeacher();
        return json.readValue(response.getResponse().getContentAsString(), Builder.class);
    }

    @Test
    void previewMediaChecksCourseAndChildOwnershipBeforeSigning() throws Exception {
        teacher.setFirstName("Lan");
        teacher.setLastName("Nguyen");
        Builder draft = chapter(create(), "Preview chapter");
        draft = lesson(draft, draft.chapters().getFirst().chapter().getId(), "Preview lesson");
        assertThat(draft.course().getAuthorDisplayName()).isEqualTo("Lan Nguyen");
        Course course = courses.findById(draft.course().getId()).orElseThrow();
        Lesson lesson = em.find(Lesson.class, draft.chapters().getFirst().lessons().getFirst().lesson().getId());
        course.setThumbnailUrl("private/cover.png");
        course.setIntroVideoUrl("private/intro.mp4");
        lesson.setVideoUrl("private/lesson.mp4");
        lesson.setIsPreview(true);
        var material = new CourseMaterial().lesson(lesson).fileType("PDF").fileUrl("private/lesson.pdf").fileName("lesson.pdf");
        em.persist(material);
        em.flush();
        List<String> paths = List.of(
            "/thumbnail/access",
            "/intro-video/playback",
            "/lessons/" + lesson.getId() + "/video/playback",
            "/lessons/" + lesson.getId() + "/materials/" + material.getId() + "/download"
        );
        for (String path : paths) {
            mvc.perform(get("/api/instructor/courses/" + course.getId() + path))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.url").value("https://signed.example/file"));
            asTeacher();
        }
        Builder otherCourse = create();
        clearInvocations(storage);
        mvc.perform(
            get("/api/instructor/courses/" + otherCourse.course().getId() + "/lessons/" + lesson.getId() + "/video/playback")
        ).andExpect(status().isNotFound());
        asTeacher();
        User other = new User();
        other.setLogin("preview-other");
        other.setPassword("x".repeat(60));
        other.setActivated(true);
        em.persist(other);
        course.setAuthor(other);
        em.flush();
        for (String path : paths) {
            mvc.perform(get("/api/instructor/courses/" + course.getId() + path)).andExpect(status().isNotFound());
            asTeacher();
        }
        verifyNoInteractions(storage);
    }

    @Test
    void thumbnailIntroAndOptionalLessonVideoUploadAccessAndRemoval() throws Exception {
        Builder draft = chapter(create(), "Media");
        draft = lesson(draft, draft.chapters().getFirst().chapter().getId(), "Optional video");
        Long lessonId = draft.chapters().getFirst().lessons().getFirst().lesson().getId();
        var png = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(new java.awt.image.BufferedImage(2, 2, java.awt.image.BufferedImage.TYPE_INT_RGB), "png", png);
        draft = uploadMedia(draft, "/thumbnail", new MockMultipartFile("file", "cover.png", "image/png", png.toByteArray()));
        assertThat(draft.course().getThumbnailUrl()).isNotNull();
        byte[] mp4 = new byte[] { 0, 0, 0, 16, 'f', 't', 'y', 'p', 'i', 's', 'o', 'm', 0, 0, 0, 0 };
        draft = uploadMedia(draft, "/intro-video", new MockMultipartFile("file", "intro.mp4", "video/mp4", mp4));
        assertThat(draft.totalVideos()).isZero();
        draft = uploadMedia(draft, "/lessons/" + lessonId + "/video", new MockMultipartFile("file", "lesson.mp4", "video/mp4", mp4));
        assertThat(draft.totalVideos()).isEqualTo(1);
        var video = draft.chapters().getFirst().lessons().getFirst().lesson();
        assertThat(video.getVideoFileName()).isEqualTo("lesson.mp4");
        assertThat(video.getDurationMinutes()).isNull();
        for (String access : List.of(
            "/thumbnail/access",
            "/intro-video/playback",
            "/lessons/" + lessonId + "/video/playback",
            "/lessons/" + lessonId + "/video/download"
        )) {
            mvc.perform(get("/api/instructor/courses/" + draft.course().getId() + access))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"));
        }
        var removed = mvc
            .perform(
                delete("/api/instructor/courses/" + draft.course().getId() + "/lessons/" + lessonId + "/video").param(
                    "version",
                    draft.course().getVersion().toString()
                )
            )
            .andExpect(status().isOk())
            .andReturn();
        draft = json.readValue(removed.getResponse().getContentAsString(), Builder.class);
        assertThat(draft.totalVideos()).isZero();
        mvc.perform(get("/api/instructor/courses/" + draft.course().getId() + "/lessons/" + lessonId + "/video/playback")).andExpect(
            status().isNotFound()
        );
    }

    @Test
    void pendingReviewLocksEvenIfCourseStillSaysDraft() throws Exception {
        Builder draft = create();
        var request = new CourseReviewRequest();
        request.setCourse(courses.findById(draft.course().getId()).orElseThrow());
        request.setCourseVersion(draft.course().getVersion());
        request.setSubmittedBy(teacher);
        request.setStatus(CourseReviewRequestStatus.PENDING);
        request.setSubmittedAt(Instant.now());
        request.setRightsConfirmedAt(Instant.now());
        em.persist(request);
        em.flush();
        mvc.perform(
            post("/api/instructor/courses/" + draft.course().getId() + "/chapters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new ChapterInput(draft.course().getVersion(), "Illegal")))
        ).andExpect(status().isConflict());
    }

    @Test
    @org.springframework.security.test.context.support.WithAnonymousUser
    void anonymousCallerMustAuthenticate() throws Exception {
        mvc.perform(post("/api/instructor/courses").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(
            status().isUnauthorized()
        );
    }
}
