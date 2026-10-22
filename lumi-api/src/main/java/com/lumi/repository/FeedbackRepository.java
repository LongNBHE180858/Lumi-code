package com.lumi.repository;

import com.lumi.domain.Feedback;
import com.lumi.domain.enumeration.FeedbackStatus;
import com.lumi.domain.enumeration.FeedbackType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Feedback entity.
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long>, JpaSpecificationExecutor<Feedback> {

    Page<Feedback> findByIsDeletedFalse(Pageable pageable);

    Page<Feedback> findByIsDeletedFalseAndStatus(FeedbackStatus status, Pageable pageable);

    Page<Feedback> findByIsDeletedFalseAndType(FeedbackType type, Pageable pageable);

    Page<Feedback> findByIsDeletedFalseAndStatusAndType(FeedbackStatus status, FeedbackType type, Pageable pageable);

    Optional<Feedback> findByIdAndIsDeletedFalse(Long id);
}
