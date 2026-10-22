package com.lumi.service;

import com.lumi.domain.enumeration.FeedbackStatus;
import com.lumi.domain.enumeration.FeedbackType;
import com.lumi.service.dto.*;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumi.domain.Feedback}.
 */
public interface FeedbackService {

    FeedbackDetailDTO createFeedback(FeedbackCreateDTO feedbackCreateDTO);

    Page<FeedbackListDTO> getAllFeedbacks(FeedbackStatus status, FeedbackType type, Pageable pageable);

    Optional<FeedbackDetailDTO> getFeedback(Long id);

    FeedbackDetailDTO updateFeedbackStatus(Long id, FeedbackStatus newStatus);

    FeedbackDetailDTO updateFeedbackNotes(Long id, String adminNotes);

    void deleteFeedback(Long id);
}
