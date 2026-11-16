package com.lumi.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.lumi.domain.*;
import com.lumi.domain.enumeration.*;
import com.lumi.repository.*;
import com.lumi.service.dto.CourseDTO;
import com.lumi.service.dto.InstructorCourseDTOs.*;
import com.lumi.service.impl.InstructorCourseService;
import com.lumi.service.mapper.*;
import com.lumi.service.query.CourseQueryService;
import com.lumi.service.storage.CourseStorageCleanup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class InstructorCourseServiceTest {

    @Mock
    CourseRepository courses;

    @Mock
    ChapterRepository chapters;

    @Mock
    LessonRepository lessons;

    @Mock
    CourseMaterialRepository materials;

    @Mock
    UserRepository users;

    @Mock
    UserProfileRepository profiles;

    @Mock
    CourseReviewRequestRepository reviews;

    @Mock
    EntityManager em;

    @Mock
    CourseStorageCleanup cleanup;

    @Mock
    CourseMapper courseMapper;

    @Mock
    CourseQueryService courseQuery;

    @Mock
    ChapterMapper chapterMapper;

    @Mock
    LessonMapper lessonMapper;

    @Mock
    CourseMaterialMapper materialMapper;

    @Spy
    @InjectMocks
    InstructorCourseService service;

    private User teacher;
    private Course course;
    private final Builder mockBuilder = new Builder(null, List.of(), 0, 0, 0);

    @BeforeEach
    void setup() {
        teacher = new User();
        teacher.setId(10L);
        teacher.setLogin("instructor1");
        teacher.setActivated(true);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(
            new UsernamePasswordAuthenticationToken("instructor1", "pass", List.of(new SimpleGrantedAuthority("ROLE_USER")))
        );
        SecurityContextHolder.setContext(context);

        lenient().when(users.findOneByLogin("instructor1")).thenReturn(Optional.of(teacher));
        UserProfile profile = new UserProfile();
        profile.setInstructorStatus(InstructorApplicationStatus.ACTIVE);
        lenient().when(profiles.findOneByUserId(10L)).thenReturn(Optional.of(profile));

        course = new Course().id(1L).author(teacher).status(RecordStatus.DRAFT).version(1L);
        course.setIsDeleted(false);

        CourseDTO courseDto = new CourseDTO();
        courseDto.setId(1L);
        courseDto.setStatus(RecordStatus.DRAFT);
        lenient()
            .when(courseMapper.toDto(any(Course.class)))
            .thenReturn(courseDto);
        lenient().when(reviews.findFirstByCourseIdOrderBySubmittedAtDescIdDesc(any())).thenReturn(Optional.empty());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // =========================================================================
    // 1. Course Creation & Basic Info (Module 1)
    // =========================================================================

    @Test
    void create_ValidTitle_Normal() {
        CreateDraft input = new CreateDraft("Lập trình Java căn bản");

        service.create(input);

        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        verify(courses).saveAndFlush(captor.capture());
        Course saved = captor.getValue();
        assertThat(saved.getTitle()).isEqualTo("Lập trình Java căn bản");
        assertThat(saved.getStatus()).isEqualTo(RecordStatus.DRAFT);
        assertThat(saved.getAuthor()).isSameAs(teacher);
        assertThat(saved.getIsDeleted()).isFalse();
    }

    @Test
    void create_BlankOrNullTitle_Boundary() {
        service.create(new CreateDraft("   "));
        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        verify(courses).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("Khóa học chưa đặt tên");
    }

    @Test
    void delete_SoftDeletesCourse_Normal() {
        doReturn(course).when(service).editable(1L, 1L);

        service.delete(1L, 1L);

        assertThat(course.getIsDeleted()).isTrue();
        verify(courses).saveAndFlush(course);
    }

    @Test
    void basicInfo_ValidInput_Normal() {
        doReturn(course).when(service).editable(1L, 1L);
        doReturn(mockBuilder).when(service).changed(course);

        BasicInfo input = new BasicInfo(
            1L,
            "Tiêu đề mới",
            null,
            null,
            "Kết nối tri thức với cuộc sống",
            "Mô tả ngắn",
            "Mô tả chi tiết",
            "Đối tượng học viên",
            "Yêu cầu tiên quyết",
            List.of("Mục tiêu 1", "Mục tiêu 2")
        );

        service.basicInfo(1L, input);

        assertThat(course.getTitle()).isEqualTo("Tiêu đề mới");
        assertThat(course.getBookSeries()).isEqualTo("Kết nối tri thức với cuộc sống");
        assertThat(course.getShortDescription()).isEqualTo("Mô tả ngắn");
        assertThat(course.getDescription()).isEqualTo("Mô tả chi tiết");
        assertThat(course.getLearningObjectives()).containsExactly("Mục tiêu 1", "Mục tiêu 2");
        assertThat(course.getPrerequisites()).isEqualTo("Yêu cầu tiên quyết");
        assertThat(course.getTargetAudience()).isEqualTo("Đối tượng học viên");
        verify(service).changed(course);
    }

    @Test
    void basicInfo_SubjectNotFound_Abnormal() {
        doReturn(course).when(service).editable(1L, 1L);
        when(em.find(Subject.class, 999L)).thenReturn(null);

        BasicInfo input = new BasicInfo(1L, "Title", 999L, null, null, null, null, null, null, null);

        assertThatThrownBy(() -> service.basicInfo(1L, input))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("subjectId not found");
    }

    @Test
    void basicInfo_GradeNotFound_Abnormal() {
        doReturn(course).when(service).editable(1L, 1L);
        when(em.find(Grade.class, 888L)).thenReturn(null);

        BasicInfo input = new BasicInfo(1L, "Title", null, 888L, null, null, null, null, null, null);

        assertThatThrownBy(() -> service.basicInfo(1L, input))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("gradeId not found");
    }

    @Test
    void basicInfo_UnsupportedBookSeries_Abnormal() {
        doReturn(course).when(service).editable(1L, 1L);
        BasicInfo input = new BasicInfo(1L, "Title", null, null, "Bộ sách không hỗ trợ", null, null, null, null, null);

        assertThatThrownBy(() -> service.basicInfo(1L, input))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Unsupported book series");
    }

    // =========================================================================
    // 2. Pricing (Module 6)
    // =========================================================================

    @Test
    void pricing_FreeCourse_Boundary() {
        doReturn(course).when(service).editable(1L, 1L);
        doReturn(mockBuilder).when(service).changed(course);

        Pricing input = new Pricing(1L, BigDecimal.ZERO, 30, AccessDurationUnit.DAY);
        service.pricing(1L, input);

        assertThat(course.getPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(course.getAccessDurationValue()).isEqualTo(30);
        assertThat(course.getAccessDurationUnit()).isEqualTo(AccessDurationUnit.DAY);
        assertThat(course.getValidDurationDays()).isEqualTo(30);
        verify(service).changed(course);
    }

    @Test
    void pricing_PaidCourse_Normal() {
        doReturn(course).when(service).editable(1L, 1L);
        doReturn(mockBuilder).when(service).changed(course);

        Pricing input = new Pricing(1L, BigDecimal.valueOf(500000), 6, AccessDurationUnit.MONTH);
        service.pricing(1L, input);

        assertThat(course.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(500000));
        assertThat(course.getAccessDurationUnit()).isEqualTo(AccessDurationUnit.MONTH);
        assertThat(course.getValidDurationDays()).isNull(); // Calendar months do not approximate days
        verify(service).changed(course);
    }

    // =========================================================================
    // 3. Curriculum & Chapter Management (Module 2)
    // =========================================================================

    @Test
    void addChapter_Success_Normal() {
        doReturn(course).when(service).editable(1L, 1L);
        doReturn(mockBuilder).when(service).changed(course);

        Chapter c1 = new Chapter().id(11L).orderIndex(0);
        when(chapters.findByCourseIdOrderByOrderIndexAscIdAsc(1L)).thenReturn(List.of(c1));

        service.addChapter(1L, new ChapterInput(1L, "Chương 2"));

        ArgumentCaptor<Chapter> captor = ArgumentCaptor.forClass(Chapter.class);
        verify(chapters).save(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("Chương 2");
        assertThat(captor.getValue().getOrderIndex()).isEqualTo(1); // 0 + 1
        assertThat(captor.getValue().getCourse()).isSameAs(course);
    }

    @Test
    void updateChapter_Success_Normal() {
        doReturn(course).when(service).editable(1L, 1L);
        doReturn(mockBuilder).when(service).changed(course);
        Chapter chapter = new Chapter().id(20L).title("Old Chapter Name");
        doReturn(chapter).when(service).chapter(1L, 20L);

        service.updateChapter(1L, 20L, new ChapterInput(1L, "New Chapter Name"));

        assertThat(chapter.getTitle()).isEqualTo("New Chapter Name");
        verify(service).changed(course);
    }

    @Test
    void deleteChapter_Success_Normal() {
        doReturn(course).when(service).editable(1L, 1L);
        doReturn(mockBuilder).when(service).changed(course);
        Chapter c1 = new Chapter().id(10L).orderIndex(0);
        Chapter c2 = new Chapter().id(20L).orderIndex(1);
        doReturn(c1).when(service).chapter(1L, 10L);
        when(lessons.findByChapterIdOrderByOrderIndexAscIdAsc(10L)).thenReturn(List.of());
        when(chapters.findByCourseIdOrderByOrderIndexAscIdAsc(1L)).thenReturn(List.of(c2));

        service.deleteChapter(1L, 10L, 1L);

        verify(chapters).delete(c1);
        assertThat(c2.getOrderIndex()).isZero(); // Reindexed
        verify(service).changed(course);
    }

    // =========================================================================
    // 4. Lesson Operations (Module 3)
    // =========================================================================

    @Test
    void addLesson_Success_Normal() {
        doReturn(course).when(service).editable(1L, 1L);
        doReturn(mockBuilder).when(service).changed(course);
        Chapter chapter = new Chapter().id(20L);
        doReturn(chapter).when(service).chapter(1L, 20L);
        Lesson l1 = new Lesson().id(101L).orderIndex(0);
        when(lessons.findByChapterIdOrderByOrderIndexAscIdAsc(20L)).thenReturn(List.of(l1));

        service.addLesson(1L, 20L, new LessonInput(1L, "Bài mới", "Mô tả", true));

        ArgumentCaptor<Lesson> captor = ArgumentCaptor.forClass(Lesson.class);
        verify(lessons).save(captor.capture());
        Lesson saved = captor.getValue();
        assertThat(saved.getTitle()).isEqualTo("Bài mới");
        assertThat(saved.getDescription()).isEqualTo("Mô tả");
        assertThat(saved.getIsPreview()).isTrue();
        assertThat(saved.getOrderIndex()).isEqualTo(1); // 0 + 1
        assertThat(saved.getStatus()).isEqualTo(RecordStatus.DRAFT);
    }

    @Test
    void updateLesson_Success_Normal() {
        doReturn(course).when(service).editable(1L, 1L);
        doReturn(mockBuilder).when(service).changed(course);
        Lesson lesson = new Lesson().id(101L).title("Old Title").isPreview(false);
        doReturn(lesson).when(service).lesson(1L, 101L);

        service.updateLesson(1L, 101L, new LessonInput(1L, "Updated Title", "New Desc", true));

        assertThat(lesson.getTitle()).isEqualTo("Updated Title");
        assertThat(lesson.getDescription()).isEqualTo("New Desc");
        assertThat(lesson.getIsPreview()).isTrue();
        verify(service).changed(course);
    }

    @Test
    @SuppressWarnings("unchecked")
    void deleteLesson_WithStudentActivity_Abnormal() {
        doReturn(course).when(service).editable(1L, 1L);
        Lesson lesson = new Lesson().id(101L).chapter(new Chapter().id(20L));
        doReturn(lesson).when(service).lesson(1L, 101L);

        TypedQuery<Long> query = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(eq("id"), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(1L); // 1 active StudentNote

        assertThatThrownBy(() -> service.deleteLesson(1L, 101L, 1L))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Lesson has learning activity and cannot be deleted");
    }

    // =========================================================================
    // 5. Review Workflow & Submission (Module 7)
    // =========================================================================

    @Test
    void submitReview_UnconfirmedRights_Abnormal() {
        doReturn(course).when(service).editable(1L, 1L);
        SubmitReview input = new SubmitReview(1L, "Ghi chú", false);

        assertThatThrownBy(() -> service.submitReview(1L, input))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Content rights must be confirmed");
    }

    @Test
    void withdrawReview_Success_Normal() {
        course.setStatus(RecordStatus.PENDING);
        when(courses.findForBuilderUpdate(1L)).thenReturn(Optional.of(course));
        CourseReviewRequest pendingRequest = new CourseReviewRequest().id(99L).course(course).status(CourseReviewRequestStatus.PENDING);
        when(reviews.findPendingForUpdate(1L)).thenReturn(Optional.of(pendingRequest));
        doReturn(mockBuilder).when(service).changed(course);

        service.withdrawReview(1L, new WithdrawReview(1L));

        assertThat(pendingRequest.getStatus()).isEqualTo(CourseReviewRequestStatus.WITHDRAWN);
        assertThat(course.getStatus()).isEqualTo(RecordStatus.DRAFT);
        verify(reviews).saveAndFlush(pendingRequest);
        verify(service).changed(course);
    }

    @Test
    void withdrawReview_CourseNotPending_Abnormal() {
        course.setStatus(RecordStatus.DRAFT); // not pending
        when(courses.findForBuilderUpdate(1L)).thenReturn(Optional.of(course));

        assertThatThrownBy(() -> service.withdrawReview(1L, new WithdrawReview(1L)))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Only a pending course review can be withdrawn");
    }
}
