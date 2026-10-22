package com.lumi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lumi.IntegrationTest;
import com.lumi.domain.Chapter;
import com.lumi.domain.Course;
import com.lumi.domain.Feedback;
import com.lumi.domain.Lesson;
import com.lumi.domain.enumeration.FeedbackStatus;
import com.lumi.domain.enumeration.FeedbackType;
import com.lumi.repository.FeedbackRepository;
import com.lumi.service.dto.FeedbackCreateDTO;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link FeedbackResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
class FeedbackResourceIT {

    private static final String DEFAULT_TITLE = "Lỗi không xem được video";
    private static final FeedbackType DEFAULT_TYPE = FeedbackType.BUG_REPORT;
    private static final String DEFAULT_CONTENT = "Video bài 3 bị đứng hình sau 15 giây dù mạng ổn định.";
    private static final String DEFAULT_CONTACT_EMAIL = "student@lumi.edu.vn";

    private static final String ENTITY_API_URL = "/api/feedbacks";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFeedbackMockMvc;

    private Course course;
    private Chapter chapter;
    private Lesson lesson;

    @BeforeEach
    public void initTest() {
        feedbackRepository.deleteAll();
    }

    private void initCourseAndLesson() {
        course = CourseResourceIT.createEntity(em);
        em.persist(course);
        em.flush();

        chapter = ChapterResourceIT.createEntity(em);
        chapter.setCourse(course);
        em.persist(chapter);
        em.flush();

        lesson = LessonResourceIT.createEntity(em);
        lesson.setChapter(chapter);
        em.persist(lesson);
        em.flush();
    }

    @Test
    @Transactional
    @WithMockUser
    void createFeedback_withMandatoryFieldsOnly() throws Exception {
        int databaseSizeBeforeCreate = feedbackRepository.findAll().size();

        FeedbackCreateDTO dto = new FeedbackCreateDTO();
        dto.setTitle(DEFAULT_TITLE);
        dto.setType(DEFAULT_TYPE);
        dto.setContent(DEFAULT_CONTENT);
        dto.setContactEmail(DEFAULT_CONTACT_EMAIL);

        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.contactEmail").value(DEFAULT_CONTACT_EMAIL))
            .andExpect(jsonPath("$.status").value(FeedbackStatus.NEW.toString()));

        List<Feedback> feedbackList = feedbackRepository.findAll();
        assertThat(feedbackList).hasSize(databaseSizeBeforeCreate + 1);
        Feedback testFeedback = feedbackList.get(feedbackList.size() - 1);
        assertThat(testFeedback.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testFeedback.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testFeedback.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testFeedback.getContactEmail()).isEqualTo(DEFAULT_CONTACT_EMAIL);
        assertThat(testFeedback.getStatus()).isEqualTo(FeedbackStatus.NEW);
        assertThat(testFeedback.getIsDeleted()).isFalse();
    }

    @Test
    @Transactional
    @WithMockUser
    void createFeedback_withCourseAndLesson() throws Exception {
        initCourseAndLesson();

        FeedbackCreateDTO dto = new FeedbackCreateDTO();
        dto.setTitle(DEFAULT_TITLE);
        dto.setType(DEFAULT_TYPE);
        dto.setContent(DEFAULT_CONTENT);
        dto.setContactEmail(DEFAULT_CONTACT_EMAIL);
        dto.setCourseId(course.getId());
        dto.setLessonId(lesson.getId());

        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.course.id").value(course.getId()))
            .andExpect(jsonPath("$.lesson.id").value(lesson.getId()));
    }

    @Test
    @Transactional
    @WithMockUser
    void createFeedback_titleValidations() throws Exception {
        // null title
        FeedbackCreateDTO dtoNull = new FeedbackCreateDTO();
        dtoNull.setTitle(null);
        dtoNull.setType(DEFAULT_TYPE);
        dtoNull.setContent(DEFAULT_CONTENT);
        dtoNull.setContactEmail(DEFAULT_CONTACT_EMAIL);

        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dtoNull)))
            .andExpect(status().isBadRequest());

        // empty title
        dtoNull.setTitle("");
        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dtoNull)))
            .andExpect(status().isBadRequest());

        // whitespace-only title
        dtoNull.setTitle("     ");
        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dtoNull)))
            .andExpect(status().isBadRequest());

        // 255 chars title (valid)
        String title255 = "A".repeat(255);
        dtoNull.setTitle(title255);
        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dtoNull)))
            .andExpect(status().isCreated());

        // 256 chars title (invalid)
        dtoNull.setTitle("A".repeat(256));
        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dtoNull)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @WithMockUser
    void createFeedback_contentBoundaryValidations() throws Exception {
        FeedbackCreateDTO dto = new FeedbackCreateDTO();
        dto.setTitle(DEFAULT_TITLE);
        dto.setType(DEFAULT_TYPE);
        dto.setContactEmail(DEFAULT_CONTACT_EMAIL);

        // 1. Không nhập: null content (invalid)
        dto.setContent(null);
        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isBadRequest());

        // 1b. Không nhập: empty string "" (invalid)
        dto.setContent("");
        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isBadRequest());

        // 2. Chỉ khoảng trắng: 10 spaces "          " (invalid)
        dto.setContent("          ");
        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isBadRequest());

        // 2b. Khoảng trắng độn nhưng nội dung thực < 10 ký tự: "12345     " (invalid)
        dto.setContent("12345     ");
        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isBadRequest());

        // 3. 9 ký tự (invalid, min is 10)
        dto.setContent("123456789");
        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isBadRequest());

        // 4. 10 ký tự (valid, min is 10)
        dto.setContent("1234567890");
        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isCreated());

        // 5. 5000 ký tự (valid, max is 5000)
        dto.setContent("A".repeat(5000));
        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isCreated());

        // 6. 5001 ký tự (invalid, max is 5000)
        dto.setContent("A".repeat(5001));
        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @WithMockUser
    void createFeedback_emailValidations() throws Exception {
        FeedbackCreateDTO dto = new FeedbackCreateDTO();
        dto.setTitle(DEFAULT_TITLE);
        dto.setType(DEFAULT_TYPE);
        dto.setContent(DEFAULT_CONTENT);

        // null email
        dto.setContactEmail(null);
        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isBadRequest());

        // invalid email format
        dto.setContactEmail("invalid-email");
        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isBadRequest());

        // valid 254 chars email (RFC 5321 limits local part to 64 chars)
        String local64 = "a".repeat(64);
        String domain189 = "@" + "b".repeat(60) + "." + "b".repeat(60) + "." + "c".repeat(63) + ".com";
        String email254 = local64 + domain189; // 64 + 1 + 60 + 1 + 60 + 1 + 63 + 4 = 254
        dto.setContactEmail(email254);
        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isCreated());

        // 255 chars email
        String domain190 = "@" + "b".repeat(60) + "." + "b".repeat(60) + "." + "c".repeat(64) + ".com";
        String email255 = local64 + domain190; // 64 + 191 = 255
        dto.setContactEmail(email255);
        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @WithMockUser
    void createFeedback_lessonWithoutCourse_shouldFail() throws Exception {
        initCourseAndLesson();

        FeedbackCreateDTO dto = new FeedbackCreateDTO();
        dto.setTitle(DEFAULT_TITLE);
        dto.setType(DEFAULT_TYPE);
        dto.setContent(DEFAULT_CONTENT);
        dto.setContactEmail(DEFAULT_CONTACT_EMAIL);
        dto.setLessonId(lesson.getId());
        dto.setCourseId(null); // Missing courseId

        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("error.lessonwithoutcourse"));
    }

    @Test
    @Transactional
    @WithMockUser
    void createFeedback_lessonNotInCourse_shouldFail() throws Exception {
        initCourseAndLesson();

        // Create a second course
        Course otherCourse = CourseResourceIT.createEntity(em);
        em.persist(otherCourse);
        em.flush();

        FeedbackCreateDTO dto = new FeedbackCreateDTO();
        dto.setTitle(DEFAULT_TITLE);
        dto.setType(DEFAULT_TYPE);
        dto.setContent(DEFAULT_CONTENT);
        dto.setContactEmail(DEFAULT_CONTACT_EMAIL);
        dto.setCourseId(otherCourse.getId()); // Lesson belongs to course, not otherCourse
        dto.setLessonId(lesson.getId());

        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("error.lessonnotinparentcourse"));
    }

    @Test
    @Transactional
    void createFeedback_anonymous_shouldReturn401() throws Exception {
        FeedbackCreateDTO dto = new FeedbackCreateDTO();
        dto.setTitle(DEFAULT_TITLE);
        dto.setType(DEFAULT_TYPE);
        dto.setContent(DEFAULT_CONTENT);
        dto.setContactEmail(DEFAULT_CONTACT_EMAIL);

        restFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isUnauthorized());
    }
}
