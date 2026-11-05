package com.lumi.repository;

import com.lumi.domain.CourseReviewRequest;
import com.lumi.domain.enumeration.CourseReviewRequestStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CourseReviewRequest entity.
 */
@Repository
public interface CourseReviewRequestRepository
    extends JpaRepository<CourseReviewRequest, Long>, JpaSpecificationExecutor<CourseReviewRequest>
{
    java.util.Optional<CourseReviewRequest> findFirstByCourseIdOrderBySubmittedAtDescIdDesc(Long courseId);
    boolean existsByCourseIdAndStatus(Long courseId, com.lumi.domain.enumeration.CourseReviewRequestStatus status);

    @Query(
        "select courseReviewRequest from CourseReviewRequest courseReviewRequest where courseReviewRequest.submittedBy.login = ?#{authentication.name}"
    )
    List<CourseReviewRequest> findBySubmittedByIsCurrentUser();

    @Query(
        "select courseReviewRequest from CourseReviewRequest courseReviewRequest where courseReviewRequest.reviewedBy.login = ?#{authentication.name}"
    )
    List<CourseReviewRequest> findByReviewedByIsCurrentUser();

    default Optional<CourseReviewRequest> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CourseReviewRequest> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CourseReviewRequest> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select courseReviewRequest from CourseReviewRequest courseReviewRequest left join fetch courseReviewRequest.submittedBy left join fetch courseReviewRequest.reviewedBy",
        countQuery = "select count(courseReviewRequest) from CourseReviewRequest courseReviewRequest"
    )
    Page<CourseReviewRequest> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select courseReviewRequest from CourseReviewRequest courseReviewRequest left join fetch courseReviewRequest.submittedBy left join fetch courseReviewRequest.reviewedBy"
    )
    List<CourseReviewRequest> findAllWithToOneRelationships();

    @Query(
        "select courseReviewRequest from CourseReviewRequest courseReviewRequest left join fetch courseReviewRequest.submittedBy left join fetch courseReviewRequest.reviewedBy where courseReviewRequest.id =:id"
    )
    Optional<CourseReviewRequest> findOneWithToOneRelationships(@Param("id") Long id);
}
