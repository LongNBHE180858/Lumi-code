package com.lumi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lumi.IntegrationTest;
import com.lumi.domain.Feedback;
import com.lumi.domain.User;
import com.lumi.domain.enumeration.FeedbackStatus;
import com.lumi.domain.enumeration.FeedbackType;
import com.lumi.repository.FeedbackRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.UserService;
import com.lumi.service.dto.FeedbackNotesUpdateDTO;
import com.lumi.service.dto.FeedbackStatusUpdateDTO;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
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
 * Integration tests for the {@link AdminFeedbackResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
class AdminFeedbackResourceIT {

    private static final String DEFAULT_TITLE = "Giao diện bài tập bị lỗi hiển thị";
    private static final FeedbackType DEFAULT_TYPE = FeedbackType.UI_UX;
    private static final String DEFAULT_CONTENT = "Các nút bấm bài tập trên màn hình nhỏ bị tràn khung hình.";
    private static final String DEFAULT_CONTACT_EMAIL = "admin_test@lumi.edu.vn";

    private static final String ADMIN_API_URL = "/api/admin/feedbacks";
    private static final String ADMIN_API_URL_ID = ADMIN_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAdminFeedbackMockMvc;

    private Feedback feedback;

    public static Feedback createEntity(EntityManager em) {
        Feedback f = new Feedback();
        f.setTitle(DEFAULT_TITLE);
        f.setType(DEFAULT_TYPE);
        f.setContent(DEFAULT_CONTENT);
        f.setContactEmail(DEFAULT_CONTACT_EMAIL);
        f.setStatus(FeedbackStatus.NEW);
        f.setIsDeleted(false);
        f.setCreatedAt(Instant.now());
        f.setUpdatedAt(Instant.now());
        return f;
    }

    @BeforeEach
    public void initTest() {
        feedbackRepository.deleteAll();
        feedback = createEntity(em);
    }

    @Test
    @Transactional
    void accessAdminEndpoints_anonymous_shouldReturn401() throws Exception {
        restAdminFeedbackMockMvc.perform(get(ADMIN_API_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_USER")
    void accessAdminEndpoints_asRoleUser_shouldReturn403() throws Exception {
        restAdminFeedbackMockMvc.perform(get(ADMIN_API_URL)).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getAllFeedbacks_asRoleAdmin_shouldSucceed() throws Exception {
        em.persist(feedback);
        em.flush();

        restAdminFeedbackMockMvc
            .perform(get(ADMIN_API_URL + "?sort=createdAt,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(header().exists("X-Total-Count"))
            .andExpect(jsonPath("$.[*].id").value(hasItem(feedback.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(FeedbackStatus.NEW.toString())));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getAllFeedbacks_unallowedSortProperty_shouldReturn400() throws Exception {
        restAdminFeedbackMockMvc
            .perform(get(ADMIN_API_URL + "?sort=password,desc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getFeedbackDetail_found() throws Exception {
        em.persist(feedback);
        em.flush();

        restAdminFeedbackMockMvc
            .perform(get(ADMIN_API_URL_ID, feedback.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(feedback.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.status").value(FeedbackStatus.NEW.toString()));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getFeedbackDetail_notFound() throws Exception {
        restAdminFeedbackMockMvc.perform(get(ADMIN_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_ADMIN")
    void updateStatus_stateTransitions() throws Exception {
        em.persist(feedback);
        em.flush();

        // 1. NEW -> RESOLVED directly is forbidden
        FeedbackStatusUpdateDTO resolveDto = new FeedbackStatusUpdateDTO();
        resolveDto.setStatus(FeedbackStatus.RESOLVED);
        restAdminFeedbackMockMvc
            .perform(patch(ADMIN_API_URL_ID + "/status", feedback.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(resolveDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("error.invalidstatustransition"));

        // 2. NEW -> IN_REVIEW is valid
        FeedbackStatusUpdateDTO inReviewDto = new FeedbackStatusUpdateDTO();
        inReviewDto.setStatus(FeedbackStatus.IN_REVIEW);
        restAdminFeedbackMockMvc
            .perform(patch(ADMIN_API_URL_ID + "/status", feedback.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(inReviewDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(FeedbackStatus.IN_REVIEW.toString()));

        // 3. IN_REVIEW -> RESOLVED is valid
        restAdminFeedbackMockMvc
            .perform(patch(ADMIN_API_URL_ID + "/status", feedback.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(resolveDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(FeedbackStatus.RESOLVED.toString()));

        // 4. RESOLVED -> DISMISSED is valid (switching between closed states)
        FeedbackStatusUpdateDTO dismissDto = new FeedbackStatusUpdateDTO();
        dismissDto.setStatus(FeedbackStatus.DISMISSED);
        restAdminFeedbackMockMvc
            .perform(patch(ADMIN_API_URL_ID + "/status", feedback.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(dismissDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(FeedbackStatus.DISMISSED.toString()));

        // 5. DISMISSED -> IN_REVIEW is valid (re-open)
        restAdminFeedbackMockMvc
            .perform(patch(ADMIN_API_URL_ID + "/status", feedback.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(inReviewDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(FeedbackStatus.IN_REVIEW.toString()));

        // 6. Cannot return to NEW
        FeedbackStatusUpdateDTO newDto = new FeedbackStatusUpdateDTO();
        newDto.setStatus(FeedbackStatus.NEW);
        restAdminFeedbackMockMvc
            .perform(patch(ADMIN_API_URL_ID + "/status", feedback.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(newDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("error.cannottesturntonew"));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_ADMIN")
    void updateNotes_shouldSucceed() throws Exception {
        em.persist(feedback);
        em.flush();

        FeedbackNotesUpdateDTO notesDto = new FeedbackNotesUpdateDTO();
        notesDto.setAdminNotes("Đã chuyển giao cho đội phát triển sửa lỗi.");

        restAdminFeedbackMockMvc
            .perform(patch(ADMIN_API_URL_ID + "/notes", feedback.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(notesDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.adminNotes").value("Đã chuyển giao cho đội phát triển sửa lỗi."));

        Feedback updated = feedbackRepository.findById(feedback.getId()).orElseThrow();
        assertThat(updated.getAdminNotes()).isEqualTo("Đã chuyển giao cho đội phát triển sửa lỗi.");
    }

    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_ADMIN")
    void softDeleteFeedback_shouldSucceed() throws Exception {
        em.persist(feedback);
        em.flush();

        restAdminFeedbackMockMvc
            .perform(delete(ADMIN_API_URL_ID, feedback.getId()))
            .andExpect(status().isNoContent());

        Feedback inDb = feedbackRepository.findById(feedback.getId()).orElseThrow();
        assertThat(inDb.getIsDeleted()).isTrue();

        // Get by ID should return 404
        restAdminFeedbackMockMvc
            .perform(get(ADMIN_API_URL_ID, feedback.getId()))
            .andExpect(status().isNotFound());

        // Should not appear in default list
        restAdminFeedbackMockMvc
            .perform(get(ADMIN_API_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[*].id").value(org.hamcrest.Matchers.not(hasItem(feedback.getId().intValue()))));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_ADMIN")
    void userPhysicalDeletion_shouldPreserveFeedbackAndSetNull() throws Exception {
        // Create an isolated user with no other entities attached
        User testUser = UserResourceIT.createEntity();
        em.persist(testUser);
        em.flush();

        feedback.setUser(testUser);
        em.persist(feedback);
        em.flush();

        Long feedbackId = feedback.getId();
        Long userId = testUser.getId();

        // Native physical deletion of user directly tests PostgreSQL ON DELETE SET NULL foreign key constraint
        em.createNativeQuery("DELETE FROM jhi_user WHERE id = :id").setParameter("id", userId).executeUpdate();
        em.flush();
        em.clear();

        // Verify user is deleted
        assertThat(userRepository.findById(userId)).isEmpty();

        // Verify feedback still exists
        Feedback preservedFeedback = feedbackRepository.findById(feedbackId).orElseThrow();
        assertThat(preservedFeedback.getUser()).isNull();
        assertThat(preservedFeedback.getContactEmail()).isEqualTo(DEFAULT_CONTACT_EMAIL);

        // Verify Admin GET returns "Tài khoản đã bị xóa"
        restAdminFeedbackMockMvc
            .perform(get(ADMIN_API_URL_ID, feedbackId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.senderIdentity").value("Tài khoản đã bị xóa"))
            .andExpect(jsonPath("$.contactEmail").value(DEFAULT_CONTACT_EMAIL));
    }
}
