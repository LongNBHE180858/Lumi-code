package com.lumi.service.impl;

import com.lumi.domain.Course;
import com.lumi.domain.CourseReviewRequest;
import com.lumi.domain.User;
import com.lumi.domain.enumeration.CourseReviewRequestStatus;
import com.lumi.domain.enumeration.NotificationCategory;
import com.lumi.domain.enumeration.RecordStatus;
import com.lumi.repository.CourseRepository;
import com.lumi.repository.CourseReviewRequestRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.SecurityUtils;
import com.lumi.service.CourseReviewRequestService;
import com.lumi.service.NotificationService;
import com.lumi.service.dto.CourseReviewRequestDTO;
import com.lumi.service.mapper.CourseReviewRequestMapper;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service Implementation for managing {@link com.lumi.domain.CourseReviewRequest}.
 */
@Service
@Transactional
public class CourseReviewRequestServiceImpl implements CourseReviewRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(CourseReviewRequestServiceImpl.class);

    private final CourseReviewRequestRepository courseReviewRequestRepository;
    private final CourseReviewRequestMapper courseReviewRequestMapper;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final NotificationWebSocketService notificationWebSocketService;

    public CourseReviewRequestServiceImpl(
        CourseReviewRequestRepository courseReviewRequestRepository,
        CourseReviewRequestMapper courseReviewRequestMapper,
        CourseRepository courseRepository,
        UserRepository userRepository,
        NotificationService notificationService,
        NotificationWebSocketService notificationWebSocketService
    ) {
        this.courseReviewRequestRepository = courseReviewRequestRepository;
        this.courseReviewRequestMapper = courseReviewRequestMapper;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.notificationWebSocketService = notificationWebSocketService;
    }

    @Override
    public CourseReviewRequestDTO save(CourseReviewRequestDTO courseReviewRequestDTO) {
        LOG.debug("Request to save CourseReviewRequest : {}", courseReviewRequestDTO);
        CourseReviewRequest courseReviewRequest = courseReviewRequestMapper.toEntity(courseReviewRequestDTO);
        syncCourseStatus(courseReviewRequest);
        courseReviewRequest = courseReviewRequestRepository.save(courseReviewRequest);
        return courseReviewRequestMapper.toDto(courseReviewRequest);
    }

    @Override
    public CourseReviewRequestDTO update(CourseReviewRequestDTO courseReviewRequestDTO) {
        LOG.debug("Request to update CourseReviewRequest : {}", courseReviewRequestDTO);
        CourseReviewRequest courseReviewRequest = courseReviewRequestMapper.toEntity(courseReviewRequestDTO);
        syncCourseStatus(courseReviewRequest);
        courseReviewRequest = courseReviewRequestRepository.save(courseReviewRequest);
        return courseReviewRequestMapper.toDto(courseReviewRequest);
    }

    @Override
    public Optional<CourseReviewRequestDTO> partialUpdate(CourseReviewRequestDTO courseReviewRequestDTO) {
        LOG.debug("Request to partially update CourseReviewRequest : {}", courseReviewRequestDTO);

        return courseReviewRequestRepository
            .findById(courseReviewRequestDTO.getId())
            .map(existingCourseReviewRequest -> {
                courseReviewRequestMapper.partialUpdate(existingCourseReviewRequest, courseReviewRequestDTO);

                if (
                    existingCourseReviewRequest.getReviewedBy() == null &&
                    existingCourseReviewRequest.getStatus() != CourseReviewRequestStatus.PENDING
                ) {
                    SecurityUtils.getCurrentUserLogin()
                        .flatMap(userRepository::findOneByLogin)
                        .ifPresent(existingCourseReviewRequest::setReviewedBy);
                }
                if (
                    existingCourseReviewRequest.getReviewedAt() == null &&
                    existingCourseReviewRequest.getStatus() != CourseReviewRequestStatus.PENDING
                ) {
                    existingCourseReviewRequest.setReviewedAt(Instant.now());
                }

                syncCourseStatus(existingCourseReviewRequest);

                return existingCourseReviewRequest;
            })
            .map(courseReviewRequestRepository::save)
            .map(courseReviewRequestMapper::toDto);
    }

    private void syncCourseStatus(CourseReviewRequest request) {
        if (request == null || request.getCourse() == null) {
            return;
        }
        Course course = request.getCourse();
        if (course.getId() != null && (course.getTitle() == null || course.getStatus() == null)) {
            course = courseRepository.findById(course.getId()).orElse(course);
        }
        Instant now = Instant.now();
        if (request.getStatus() == CourseReviewRequestStatus.APPROVED) {
            course.setStatus(RecordStatus.ACTIVE);
            course.setUpdatedAt(now);
            courseRepository.save(course);
        } else if (
            request.getStatus() == CourseReviewRequestStatus.REJECTED || request.getStatus() == CourseReviewRequestStatus.WITHDRAWN
        ) {
            course.setStatus(RecordStatus.DRAFT);
            course.setUpdatedAt(now);
            courseRepository.save(course);
        } else if (request.getStatus() == CourseReviewRequestStatus.PENDING) {
            course.setStatus(RecordStatus.PENDING);
            course.setUpdatedAt(now);
            courseRepository.save(course);
        }
    }

    @Override
    public CourseReviewRequestDTO approve(Long id, String feedback) {
        LOG.debug("Request to approve CourseReviewRequest : {}, feedback: {}", id, feedback);
        CourseReviewRequest request = courseReviewRequestRepository
            .findOneWithToOneRelationships(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CourseReviewRequest not found"));

        if (request.getStatus() != CourseReviewRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ có thể phê duyệt yêu cầu ở trạng thái Chờ duyệt.");
        }

        Instant now = Instant.now();
        request.setStatus(CourseReviewRequestStatus.APPROVED);
        request.setReviewedAt(now);
        request.setReviewerFeedback(feedback == null || feedback.isBlank() ? "Đã kiểm tra và phê duyệt khóa học." : feedback.trim());

        SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).ifPresent(request::setReviewedBy);

        Course course = request.getCourse();
        if (course != null) {
            course.setStatus(RecordStatus.ACTIVE);
            course.setUpdatedAt(now);
            courseRepository.save(course);
        }

        request = courseReviewRequestRepository.save(request);

        // Notify course author / submitter
        try {
            User teacher = request.getSubmittedBy();
            if (teacher == null && course != null) {
                teacher = course.getAuthor();
            }
            if (teacher != null) {
                String courseTitle = course != null ? course.getTitle() : "Khóa học";
                notificationService.createAndSendNotification(
                    teacher,
                    "Khóa học đã được phê duyệt",
                    "Chúc mừng! Khóa học \"" + courseTitle + "\" đã được phê duyệt và chính thức xuất bản.",
                    NotificationCategory.COURSE,
                    "COURSE_APPROVED",
                    "/instructor/courses",
                    "COURSE",
                    course != null ? course.getId() : null
                );
            }
        } catch (Exception e) {
            LOG.warn("Failed to send approval notification: {}", e.getMessage());
        }

        return courseReviewRequestMapper.toDto(request);
    }

    @Override
    public CourseReviewRequestDTO reject(Long id, String reason) {
        LOG.debug("Request to reject CourseReviewRequest : {}, reason: {}", id, reason);
        CourseReviewRequest request = courseReviewRequestRepository
            .findOneWithToOneRelationships(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CourseReviewRequest not found"));

        if (request.getStatus() != CourseReviewRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ có thể từ chối yêu cầu ở trạng thái Chờ duyệt.");
        }

        if (reason == null || reason.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cần cung cấp lý do/phản hồi khi từ chối khóa học.");
        }

        Instant now = Instant.now();
        request.setStatus(CourseReviewRequestStatus.REJECTED);
        request.setReviewedAt(now);
        request.setReviewerFeedback(reason.trim());

        SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).ifPresent(request::setReviewedBy);

        Course course = request.getCourse();
        if (course != null) {
            course.setStatus(RecordStatus.DRAFT);
            course.setUpdatedAt(now);
            courseRepository.save(course);
        }

        request = courseReviewRequestRepository.save(request);

        // Notify course author / submitter
        try {
            User teacher = request.getSubmittedBy();
            if (teacher == null && course != null) {
                teacher = course.getAuthor();
            }
            if (teacher != null) {
                String courseTitle = course != null ? course.getTitle() : "Khóa học";
                notificationService.createAndSendNotification(
                    teacher,
                    "Khóa học cần chỉnh sửa thêm",
                    "Khóa học \"" + courseTitle + "\" chưa được thông qua. Phản hồi: " + reason.trim(),
                    NotificationCategory.COURSE,
                    "COURSE_REJECTED",
                    "/instructor/courses",
                    "COURSE",
                    course != null ? course.getId() : null
                );
            }
        } catch (Exception e) {
            LOG.warn("Failed to send rejection notification: {}", e.getMessage());
        }

        return courseReviewRequestMapper.toDto(request);
    }

    public Page<CourseReviewRequestDTO> findAllWithEagerRelationships(Pageable pageable) {
        return courseReviewRequestRepository.findAllWithEagerRelationships(pageable).map(courseReviewRequestMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseReviewRequestDTO> findOne(Long id) {
        LOG.debug("Request to get CourseReviewRequest : {}", id);
        return courseReviewRequestRepository.findOneWithEagerRelationships(id).map(courseReviewRequestMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CourseReviewRequest : {}", id);
        courseReviewRequestRepository.deleteById(id);
    }

    @Override
    public CourseReviewRequestDTO hideCourse(Long courseId, String reason) {
        LOG.debug("Request to hide course: {}, reason: {}", courseId, reason);
        Course course = courseRepository
            .findById(courseId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khóa học"));
        if (Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Khóa học không tồn tại");
        }
        course.setStatus(RecordStatus.HIDDEN);
        courseRepository.saveAndFlush(course);

        // Notify author
        User author = course.getAuthor();
        if (author != null) {
            try {
                String reasonText = reason != null && !reason.isBlank() ? reason.trim() : "Quản trị viên tạm ẩn để kiểm tra chất lượng.";
                notificationService.createAndSendNotification(
                    author,
                    "Khóa học đã bị tạm ẩn",
                    "Khóa học '" + course.getTitle() + "' đã bị tạm ẩn bởi quản trị viên. Lý do: " + reasonText,
                    NotificationCategory.COURSE,
                    "COURSE_HIDDEN",
                    "/instructor/courses",
                    "COURSE",
                    course.getId()
                );
            } catch (Exception e) {
                LOG.warn("Failed to send hide course notification: {}", e.getMessage());
            }
        }

        // Real-time broadcast
        try {
            notificationWebSocketService.broadcastToTopic(
                "/topic/course-review/course/" + courseId,
                Map.of("courseId", courseId, "status", "HIDDEN", "action", "HIDE", "reason", reason != null ? reason : "")
            );
        } catch (Exception e) {
            LOG.warn("Failed to broadcast hide course event: {}", e.getMessage());
        }

        CourseReviewRequest latest = courseReviewRequestRepository.findFirstByCourseIdOrderBySubmittedAtDescIdDesc(courseId).orElse(null);
        return latest != null ? courseReviewRequestMapper.toDto(latest) : null;
    }

    @Override
    public CourseReviewRequestDTO unhideCourse(Long courseId) {
        LOG.debug("Request to unhide course: {}", courseId);
        Course course = courseRepository
            .findById(courseId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khóa học"));
        if (Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Khóa học không tồn tại");
        }
        course.setStatus(RecordStatus.ACTIVE);
        courseRepository.saveAndFlush(course);

        // Notify author
        User author = course.getAuthor();
        if (author != null) {
            try {
                notificationService.createAndSendNotification(
                    author,
                    "Khóa học đã được mở lại",
                    "Khóa học '" + course.getTitle() + "' đã được mở lại và hoạt động bình thường trên hệ thống.",
                    NotificationCategory.COURSE,
                    "COURSE_ACTIVE",
                    "/instructor/courses",
                    "COURSE",
                    course.getId()
                );
            } catch (Exception e) {
                LOG.warn("Failed to send unhide course notification: {}", e.getMessage());
            }
        }

        // Real-time broadcast
        try {
            notificationWebSocketService.broadcastToTopic(
                "/topic/course-review/course/" + courseId,
                Map.of("courseId", courseId, "status", "ACTIVE", "action", "UNHIDE")
            );
        } catch (Exception e) {
            LOG.warn("Failed to broadcast unhide course event: {}", e.getMessage());
        }

        CourseReviewRequest latest = courseReviewRequestRepository.findFirstByCourseIdOrderBySubmittedAtDescIdDesc(courseId).orElse(null);
        return latest != null ? courseReviewRequestMapper.toDto(latest) : null;
    }

    @Override
    public CourseReviewRequestDTO requestRevision(Long courseId, String reason) {
        LOG.debug("Request revision for course: {}, reason: {}", courseId, reason);
        Course course = courseRepository
            .findById(courseId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khóa học"));
        if (Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Khóa học không tồn tại");
        }

        // Switch to DRAFT so instructor can edit
        course.setStatus(RecordStatus.DRAFT);
        courseRepository.saveAndFlush(course);

        // Update latest review request or add reviewer feedback
        CourseReviewRequest latest = courseReviewRequestRepository.findFirstByCourseIdOrderBySubmittedAtDescIdDesc(courseId).orElse(null);
        if (latest != null) {
            latest.setReviewerFeedback(reason);
            courseReviewRequestRepository.saveAndFlush(latest);
        }

        // Notify author
        User author = course.getAuthor();
        if (author != null) {
            try {
                String reasonText =
                    reason != null && !reason.isBlank() ? reason.trim() : "Vui lòng kiểm tra và cập nhật lại nội dung bài giảng.";
                notificationService.createAndSendNotification(
                    author,
                    "Yêu cầu chỉnh sửa khóa học",
                    "Quản trị viên yêu cầu chỉnh sửa khóa học '" + course.getTitle() + "'. Lý do / Góp ý: " + reasonText,
                    NotificationCategory.COURSE,
                    "COURSE_REVISION_REQUESTED",
                    "/instructor/courses/" + courseId + "/builder",
                    "COURSE",
                    course.getId()
                );
            } catch (Exception e) {
                LOG.warn("Failed to send revision request notification: {}", e.getMessage());
            }
        }

        // Real-time broadcast
        try {
            notificationWebSocketService.broadcastToTopic(
                "/topic/course-review/course/" + courseId,
                Map.of("courseId", courseId, "status", "DRAFT", "action", "REQUEST_REVISION", "reason", reason != null ? reason : "")
            );
        } catch (Exception e) {
            LOG.warn("Failed to broadcast revision event: {}", e.getMessage());
        }

        return latest != null ? courseReviewRequestMapper.toDto(latest) : null;
    }
}
