package com.lumi.repository;

import com.lumi.domain.CourseReviewRequest;
import com.lumi.domain.enumeration.CourseReviewRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseReviewRequestRepository extends JpaRepository<CourseReviewRequest, Long> {
    java.util.Optional<CourseReviewRequest> findFirstByCourseIdOrderBySubmittedAtDescIdDesc(Long courseId);
    boolean existsByCourseIdAndStatus(Long courseId, com.lumi.domain.enumeration.CourseReviewRequestStatus status);
}
