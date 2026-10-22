package com.lumi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.lumi.IntegrationTest;
import com.lumi.domain.*;
import com.lumi.domain.enumeration.*;
import com.lumi.service.storage.CourseObjectStorage;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/** Real separate transactions exercise PostgreSQL locking rather than sequential mock requests. */
@IntegrationTest
@AutoConfigureMockMvc
class CourseReviewSubmissionIT {

    @Autowired
    MockMvc mvc;

    @Autowired
    EntityManager em;

    @Autowired
    PlatformTransactionManager transactions;

    @MockitoBean
    CourseObjectStorage storage;

    private Long courseId, userId, subjectId, gradeId;
    private Long version;
    private String login;

    @BeforeEach
    void fixture() {
        login = "review-" + UUID.randomUUID().toString().substring(0, 12);
        new TransactionTemplate(transactions).executeWithoutResult(status -> {
            User teacher = new User();
            teacher.setLogin(login);
            teacher.setActivated(true);
            teacher.setPassword("x".repeat(60));
            em.persist(teacher);
            userId = teacher.getId();
            em.persist(new UserProfile().user(teacher).instructorStatus(InstructorApplicationStatus.ACTIVE));
            Subject subject = new Subject().name("Concurrent review subject");
            em.persist(subject);
            subjectId = subject.getId();
            Grade grade = new Grade().level(9).name("Grade 9");
            em.persist(grade);
            gradeId = grade.getId();
            Course course = new Course()
                .title("Concurrent submission")
                .author(teacher)
                .status(RecordStatus.DRAFT)
                .isDeleted(false)
                .subject(subject)
                .grade(grade)
                .shortDescription("Summary")
                .description("Full description")
                .targetAudience("Students")
                .learningObjectives(List.of("Learn"))
                .thumbnailUrl("private/cover")
                .price(BigDecimal.ZERO)
                .accessDurationValue(1)
                .accessDurationUnit(AccessDurationUnit.MONTH);
            em.persist(course);
            Chapter chapter = new Chapter().course(course).title("Chapter").orderIndex(0);
            em.persist(chapter);
            em.persist(new Lesson().chapter(chapter).title("Lesson").contentType("VIDEO").orderIndex(0).videoUrl("private/video"));
            em.flush();
            courseId = course.getId();
            version = course.getVersion();
        });
    }

    @AfterEach
    void cleanupFixture() {
        if (courseId == null) return;
        new TransactionTemplate(transactions).executeWithoutResult(status -> {
            em.createQuery("delete from CourseReviewRequest r where r.course.id = :id").setParameter("id", courseId).executeUpdate();
            em.createQuery("delete from Lesson l where l.chapter.course.id = :id").setParameter("id", courseId).executeUpdate();
            em.createQuery("delete from Chapter c where c.course.id = :id").setParameter("id", courseId).executeUpdate();
            em.remove(em.find(Course.class, courseId));
            em.createQuery("delete from UserProfile p where p.user.id = :id").setParameter("id", userId).executeUpdate();
            em.remove(em.find(User.class, userId));
            em.remove(em.find(Subject.class, subjectId));
            em.remove(em.find(Grade.class, gradeId));
        });
    }

    @Test
    void simultaneousTabsCreateExactlyOnePendingRequest() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        try {
            Callable<Integer> submit = () -> {
                if (!start.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("Concurrent test did not start");
                return mvc
                    .perform(
                        post("/api/instructor/courses/" + courseId + "/review-requests")
                            .with(user(login).roles("USER"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"version\":" + version + ",\"confirmedRights\":true,\"note\":\"Concurrent tab\"}")
                    )
                    .andReturn()
                    .getResponse()
                    .getStatus();
            };
            Future<Integer> first = pool.submit(submit),
                second = pool.submit(submit);
            start.countDown();
            assertThat(List.of(first.get(30, TimeUnit.SECONDS), second.get(30, TimeUnit.SECONDS))).containsExactlyInAnyOrder(201, 409);
            new TransactionTemplate(transactions).executeWithoutResult(status -> {
                assertThat(em.find(Course.class, courseId).getStatus()).isEqualTo(RecordStatus.PENDING);
                assertThat(
                    em
                        .createQuery("select count(r) from CourseReviewRequest r where r.course.id = :id", Long.class)
                        .setParameter("id", courseId)
                        .getSingleResult()
                ).isEqualTo(1L);
            });
        } finally {
            pool.shutdownNow();
        }
    }
}
