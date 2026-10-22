package com.lumi.service.impl;

import com.lumi.domain.Course;
import com.lumi.domain.Feedback;
import com.lumi.domain.Lesson;
import com.lumi.domain.enumeration.FeedbackStatus;
import com.lumi.domain.enumeration.FeedbackType;
import com.lumi.repository.CourseRepository;
import com.lumi.repository.FeedbackRepository;
import com.lumi.repository.LessonRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.SecurityUtils;
import com.lumi.service.FeedbackService;
import com.lumi.service.dto.*;
import com.lumi.service.mapper.FeedbackMapper;
import com.lumi.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
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
 * Service Implementation for managing {@link Feedback}.
 */
@Service
@Transactional
public class FeedbackServiceImpl implements FeedbackService {

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackServiceImpl.class);

    private static final String ENTITY_NAME = "feedback";

    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;

    public FeedbackServiceImpl(
        FeedbackRepository feedbackRepository,
        FeedbackMapper feedbackMapper,
        UserRepository userRepository,
        CourseRepository courseRepository,
        LessonRepository lessonRepository
    ) {
        this.feedbackRepository = feedbackRepository;
        this.feedbackMapper = feedbackMapper;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    public FeedbackDetailDTO createFeedback(FeedbackCreateDTO feedbackCreateDTO) {
        LOG.debug("Request to create Feedback : {}", feedbackCreateDTO);

        String trimmedContent = feedbackCreateDTO.getContent() != null ? feedbackCreateDTO.getContent().trim() : "";
        if (trimmedContent.length() < 10) {
            throw new BadRequestAlertException("Content must be at least 10 characters", ENTITY_NAME, "contenttooshort");
        }
        if (feedbackCreateDTO.getContent().length() > 5000) {
            throw new BadRequestAlertException("Content cannot exceed 5000 characters", ENTITY_NAME, "contenttoolong");
        }

        Feedback feedback = new Feedback();
        feedback.setTitle(feedbackCreateDTO.getTitle().trim());
        feedback.setType(feedbackCreateDTO.getType());
        feedback.setContent(trimmedContent);
        feedback.setContactEmail(feedbackCreateDTO.getContactEmail().trim());
        feedback.setStatus(FeedbackStatus.NEW);
        feedback.setIsDeleted(false);
        feedback.setCreatedAt(Instant.now());
        feedback.setUpdatedAt(Instant.now());

        // Associate authenticated user
        SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).ifPresent(feedback::setUser);

        // Validate Course & Lesson integrity
        if (feedbackCreateDTO.getLessonId() != null) {
            if (feedbackCreateDTO.getCourseId() == null) {
                throw new BadRequestAlertException("Lesson requires course", ENTITY_NAME, "lessonwithoutcourse");
            }
            Lesson lesson = lessonRepository
                .findById(feedbackCreateDTO.getLessonId())
                .orElseThrow(() -> new BadRequestAlertException("Lesson not found", ENTITY_NAME, "lessonnotfound"));

            if (
                lesson.getChapter() == null ||
                lesson.getChapter().getCourse() == null ||
                !lesson.getChapter().getCourse().getId().equals(feedbackCreateDTO.getCourseId())
            ) {
                throw new BadRequestAlertException("Lesson does not belong to Course", ENTITY_NAME, "lessonnotinparentcourse");
            }
            feedback.setLesson(lesson);
        }

        if (feedbackCreateDTO.getCourseId() != null) {
            Course course = courseRepository
                .findById(feedbackCreateDTO.getCourseId())
                .orElseThrow(() -> new BadRequestAlertException("Course not found", ENTITY_NAME, "coursenotfound"));
            feedback.setCourse(course);
        }

        Feedback saved = feedbackRepository.save(feedback);
        return feedbackMapper.toDetailDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FeedbackListDTO> getAllFeedbacks(FeedbackStatus status, FeedbackType type, Pageable pageable) {
        LOG.debug("Request to get all Feedbacks by status: {}, type: {}", status, type);
        Page<Feedback> page;
        if (status != null && type != null) {
            page = feedbackRepository.findByIsDeletedFalseAndStatusAndType(status, type, pageable);
        } else if (status != null) {
            page = feedbackRepository.findByIsDeletedFalseAndStatus(status, pageable);
        } else if (type != null) {
            page = feedbackRepository.findByIsDeletedFalseAndType(type, pageable);
        } else {
            page = feedbackRepository.findByIsDeletedFalse(pageable);
        }
        return page.map(feedbackMapper::toListDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FeedbackDetailDTO> getFeedback(Long id) {
        LOG.debug("Request to get Feedback : {}", id);
        return feedbackRepository.findByIdAndIsDeletedFalse(id).map(feedbackMapper::toDetailDto);
    }

    @Override
    public FeedbackDetailDTO updateFeedbackStatus(Long id, FeedbackStatus newStatus) {
        LOG.debug("Request to update Feedback status : id={}, status={}", id, newStatus);
        Feedback feedback = feedbackRepository
            .findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        FeedbackStatus currentStatus = feedback.getStatus();

        // Rule: Cannot return to NEW status
        if (newStatus == FeedbackStatus.NEW) {
            throw new BadRequestAlertException("Cannot return to NEW status", ENTITY_NAME, "cannottesturntonew");
        }

        // Rule: NEW can only transition to IN_REVIEW; skipping to RESOLVED or DISMISSED is forbidden
        if (currentStatus == FeedbackStatus.NEW && (newStatus == FeedbackStatus.RESOLVED || newStatus == FeedbackStatus.DISMISSED)) {
            throw new BadRequestAlertException(
                "Cannot transition directly from NEW to " + newStatus,
                ENTITY_NAME,
                "invalidstatustransition"
            );
        }

        feedback.setStatus(newStatus);
        feedback.setUpdatedAt(Instant.now());
        Feedback updated = feedbackRepository.save(feedback);
        return feedbackMapper.toDetailDto(updated);
    }

    @Override
    public FeedbackDetailDTO updateFeedbackNotes(Long id, String adminNotes) {
        LOG.debug("Request to update Feedback adminNotes : id={}, adminNotes={}", id, adminNotes);
        Feedback feedback = feedbackRepository
            .findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        feedback.setAdminNotes(adminNotes);
        feedback.setUpdatedAt(Instant.now());
        Feedback updated = feedbackRepository.save(feedback);
        return feedbackMapper.toDetailDto(updated);
    }

    @Override
    public void deleteFeedback(Long id) {
        LOG.debug("Request to soft delete Feedback : {}", id);
        Feedback feedback = feedbackRepository
            .findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        feedback.setIsDeleted(true);
        feedback.setUpdatedAt(Instant.now());
        feedbackRepository.save(feedback);
    }
}
