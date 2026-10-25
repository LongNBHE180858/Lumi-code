package com.lumi.service.impl;

import com.lumi.domain.Chapter;
import com.lumi.domain.Course;
import com.lumi.domain.CourseReview;
import com.lumi.domain.Grade;
import com.lumi.domain.Lesson;
import com.lumi.domain.Subject;
import com.lumi.domain.User;
import com.lumi.domain.enumeration.RecordStatus;
import com.lumi.repository.ChapterRepository;
import com.lumi.repository.CourseRepository;
import com.lumi.repository.CourseReviewRepository;
import com.lumi.repository.LessonRepository;
import com.lumi.service.CourseService;
import com.lumi.service.UserProfileService;
import com.lumi.service.dto.CourseBrowseRequest;
import com.lumi.service.dto.CourseBrowseResponse;
import com.lumi.service.dto.CourseDTO;
import com.lumi.service.dto.CourseOverviewResponse;
import com.lumi.service.dto.CourseRatingSummaryDTO;
import com.lumi.service.dto.CourseReviewsResponse;
import com.lumi.service.dto.InstructorPublicProfileDTO;
import com.lumi.service.mapper.CourseMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service Implementation for managing {@link com.lumi.domain.Course}.
 */
@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private static final Logger LOG = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final CourseReviewRepository courseReviewRepository;
    private final ChapterRepository chapterRepository;
    private final UserProfileService userProfileService;

    private final CourseMapper courseMapper;

    public CourseServiceImpl(
        CourseRepository courseRepository,
        CourseMapper courseMapper,
        LessonRepository lessonRepository,
        CourseReviewRepository courseReviewRepository,
        ChapterRepository chapterRepository,
        UserProfileService userProfileService
    ) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
        this.lessonRepository = lessonRepository;
        this.courseReviewRepository = courseReviewRepository;
        this.chapterRepository = chapterRepository;
        this.userProfileService = userProfileService;
    }

    @Override
    public CourseDTO save(CourseDTO courseDTO) {
        LOG.debug("Request to save Course : {}", courseDTO);
        Course course = courseMapper.toEntity(courseDTO);
        course = courseRepository.saveAndFlush(course);
        return courseMapper.toDto(course);
    }

    @Override
    public CourseDTO update(CourseDTO courseDTO) {
        LOG.debug("Request to update Course : {}", courseDTO);
        if (courseDTO.getVersion() == null) {
            throw new org.springframework.orm.ObjectOptimisticLockingFailureException(Course.class, courseDTO.getId());
        }
        Course course = courseMapper.toEntity(courseDTO);
        course = courseRepository.saveAndFlush(course);
        return courseMapper.toDto(course);
    }

    @Override
    public Optional<CourseDTO> partialUpdate(CourseDTO courseDTO) {
        LOG.debug("Request to partially update Course : {}", courseDTO);

        return courseRepository
            .findById(courseDTO.getId())
            .map(existingCourse -> {
                if (courseDTO.getVersion() == null || !courseDTO.getVersion().equals(existingCourse.getVersion())) {
                    throw new org.springframework.orm.ObjectOptimisticLockingFailureException(Course.class, courseDTO.getId());
                }
                courseMapper.partialUpdate(existingCourse, courseDTO);

                return existingCourse;
            })
            .map(courseRepository::saveAndFlush)
            .map(courseMapper::toDto);
    }

    public Page<CourseDTO> findAllWithEagerRelationships(Pageable pageable) {
        return courseRepository.findAllWithEagerRelationships(pageable).map(courseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseDTO> findOne(Long id) {
        LOG.debug("Request to get Course : {}", id);
        return courseRepository.findOneWithEagerRelationships(id).map(courseMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Course : {}", id);
        courseRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseBrowseResponse browse(CourseBrowseRequest request, Pageable pageable) {
        String keyword = request.getKeyword() == null ? "" : request.getKeyword().trim();
        String feeType = request.getFeeType() == null ? "ALL" : request.getFeeType().name();
        boolean filterGrades = request.getGradeIds() != null && !request.getGradeIds().isEmpty();
        List<Long> gradeIds = filterGrades ? request.getGradeIds() : List.of(0L);
        int pageSize = Math.min(pageable.getPageSize(), 50);

        Pageable paging = PageRequest.of(pageable.getPageNumber(), pageSize, Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));

        Page<Course> courses = courseRepository.browse(keyword, request.getSubjectId(), filterGrades, gradeIds, feeType, paging);

        Map<Long, Long> lessonCounts = getLessonCounts(courses.getContent());

        List<CourseBrowseResponse.CourseItem> content = new ArrayList<>();

        for (Course course : courses.getContent()) {
            Long lessonCount = lessonCounts.getOrDefault(course.getId(), 0L);

            CourseBrowseResponse.CourseItem item = toBrowseItem(course, lessonCount);

            content.add(item);
        }

        return CourseBrowseResponse.builder()
            .content(content)
            .page(courses.getNumber())
            .size(courses.getSize())
            .totalElements(courses.getTotalElements())
            .totalPages(courses.getTotalPages())
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseOverviewResponse overview(Long id) {
        Course course = getPublicCourse(id);
        List<Chapter> chapters = chapterRepository.findByCourseIdOrderByOrderIndexAscIdAsc(id);
        List<Lesson> lessons = lessonRepository.findActiveLessonsByCourseId(id);
        Map<Long, List<CourseOverviewResponse.LessonItem>> lessonsByChapter = new HashMap<>();

        for (Lesson lesson : lessons) {
            CourseOverviewResponse.LessonItem item = CourseOverviewResponse.LessonItem.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .orderIndex(lesson.getOrderIndex())
                .contentType(lesson.getContentType())
                .durationMinutes(lesson.getDurationMinutes())
                .build();
            lessonsByChapter.computeIfAbsent(lesson.getChapter().getId(), key -> new ArrayList<>()).add(item);
        }

        List<CourseOverviewResponse.ChapterItem> chapterItems = new ArrayList<>();
        for (Chapter chapter : chapters) {
            List<CourseOverviewResponse.LessonItem> chapterLessons = lessonsByChapter.getOrDefault(chapter.getId(), List.of());
            chapterItems.add(
                CourseOverviewResponse.ChapterItem.builder()
                    .id(chapter.getId())
                    .title(chapter.getTitle())
                    .orderIndex(chapter.getOrderIndex())
                    .lessonCount(chapterLessons.size())
                    .lessons(chapterLessons)
                    .build()
            );
        }

        Subject subject = course.getSubject();
        Grade grade = course.getGrade();
        return CourseOverviewResponse.builder()
            .id(course.getId())
            .title(course.getTitle())
            .description(course.getDescription())
            .thumbnailUrl(course.getThumbnailUrl())
            .subjectId(subject == null ? null : subject.getId())
            .subjectName(subject == null ? null : subject.getName())
            .gradeId(grade.getId())
            .gradeLevel(grade.getLevel())
            .gradeName(grade.getName())
            .price(course.getPrice())
            .validDurationDays(course.getValidDurationDays())
            .author(getPublicAuthor(course.getAuthor()))
            .chapterCount(chapterItems.size())
            .lessonCount(lessons.size())
            .ratingSummary(getRatingSummary(id))
            .chapters(chapterItems)
            .build();
    }

    private InstructorPublicProfileDTO getPublicAuthor(User author) {
        if (author == null) {
            return null;
        }
        try {
            return userProfileService.getPublicInstructorProfile(author.getId());
        } catch (ResponseStatusException exception) {
            if (exception.getStatusCode().value() == 404) {
                return null;
            }
            throw exception;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CourseReviewsResponse reviews(Long id, Pageable pageable) {
        getPublicCourse(id);

        Page<CourseReview> reviews = courseReviewRepository.findPageByCourseId(id, getReviewPageable(pageable));

        return CourseReviewsResponse.builder()
            .courseId(id)
            .ratingSummary(getRatingSummary(id))
            .content(reviews.map(this::toReviewItem).getContent())
            .page(reviews.getNumber())
            .size(reviews.getSize())
            .totalElements(reviews.getTotalElements())
            .totalPages(reviews.getTotalPages())
            .build();
    }

    private Course getPublicCourse(Long id) {
        return courseRepository
            .findOneWithEagerRelationships(id)
            .filter(
                course ->
                    course.getStatus() == RecordStatus.ACTIVE &&
                    !Boolean.TRUE.equals(course.getIsDeleted()) &&
                    course.getGrade() != null &&
                    course.getGrade().getLevel() != null &&
                    course.getGrade().getLevel() >= 1 &&
                    course.getGrade().getLevel() <= 9
            )
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khóa học"));
    }

    private CourseRatingSummaryDTO getRatingSummary(Long courseId) {
        CourseReviewRepository.RatingSummary summary = courseReviewRepository.getRatingSummaryByCourseId(courseId);
        long totalReviews = summary.getTotalReviews();
        BigDecimal averageRating =
            totalReviews == 0
                ? null
                : BigDecimal.valueOf(summary.getTotalRating()).divide(BigDecimal.valueOf(totalReviews), 1, RoundingMode.HALF_UP);

        return CourseRatingSummaryDTO.builder()
            .averageRating(averageRating)
            .totalReviews(totalReviews)
            .oneStarCount(summary.getOneStarCount())
            .twoStarCount(summary.getTwoStarCount())
            .threeStarCount(summary.getThreeStarCount())
            .fourStarCount(summary.getFourStarCount())
            .fiveStarCount(summary.getFiveStarCount())
            .build();
    }

    private Pageable getReviewPageable(Pageable pageable) {
        if (pageable == null || pageable.isUnpaged()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng cung cấp thông tin phân trang");
        }

        Sort sort = pageable.getSort();
        if (sort.isUnsorted()) {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        for (Sort.Order order : sort) {
            if (!Set.of("createdAt", "rating", "id").contains(order.getProperty())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ hỗ trợ sắp xếp theo createdAt, rating hoặc id");
            }
        }

        if (sort.getOrderFor("id") == null) {
            sort = sort.and(Sort.by(Sort.Direction.DESC, "id"));
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    private CourseReviewsResponse.ReviewItem toReviewItem(CourseReview review) {
        User student = review.getStudent();
        String fullName =
            student == null
                ? ""
                : (Objects.toString(student.getFirstName(), "").trim() + " " + Objects.toString(student.getLastName(), "").trim()).trim();

        return CourseReviewsResponse.ReviewItem.builder()
            .id(review.getId())
            .displayName(fullName.isEmpty() ? "Học viên" : fullName)
            .imageUrl(student == null ? null : student.getImageUrl())
            .rating(review.getRating())
            .content(review.getContent())
            .createdAt(review.getCreatedAt())
            .build();
    }

    //ham browse
    private Map<Long, Long> getLessonCounts(List<Course> courses) {
        Map<Long, Long> lessonCounts = new HashMap<>();

        if (courses.isEmpty()) {
            return lessonCounts;
        }

        List<Long> courseIds = new ArrayList<>();

        for (Course course : courses) {
            courseIds.add(course.getId());
        }

        List<LessonRepository.CourseLessonCount> results = lessonRepository.countActiveLessonsByCourseIds(courseIds);

        for (LessonRepository.CourseLessonCount result : results) {
            lessonCounts.put(result.getCourseId(), result.getLessonCount());
        }

        return lessonCounts;
    }

    private CourseBrowseResponse.CourseItem toBrowseItem(Course course, Long lessonCount) {
        Subject subject = course.getSubject();
        Grade grade = course.getGrade();
        User author = course.getAuthor();

        String authorName = null;

        if (author != null) {
            String firstName = "";
            String lastName = "";

            if (author.getFirstName() != null) {
                firstName = author.getFirstName().trim();
            }

            if (author.getLastName() != null) {
                lastName = author.getLastName().trim();
            }

            String fullName = (firstName + " " + lastName).trim();

            if (!fullName.isEmpty()) {
                authorName = fullName;
            }
        }

        return CourseBrowseResponse.CourseItem.builder()
            .id(course.getId())
            .title(course.getTitle())
            .subjectId(subject == null ? null : subject.getId())
            .subjectName(subject == null ? null : subject.getName())
            .gradeId(grade == null ? null : grade.getId())
            .gradeLevel(grade == null ? null : grade.getLevel())
            .gradeName(grade == null ? null : grade.getName())
            .authorDisplayName(authorName)
            .price(course.getPrice())
            .lessonCount(lessonCount)
            .build();
    }
}
