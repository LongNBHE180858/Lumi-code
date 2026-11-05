package com.lumi.repository;

import com.lumi.domain.AssignmentSubmission;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AssignmentSubmission entity.
 */
@Repository
public interface AssignmentSubmissionRepository
    extends JpaRepository<AssignmentSubmission, Long>, JpaSpecificationExecutor<AssignmentSubmission>
{
    @Query(
        "select assignmentSubmission from AssignmentSubmission assignmentSubmission where assignmentSubmission.student.login = ?#{authentication.name}"
    )
    List<AssignmentSubmission> findByStudentIsCurrentUser();

    @Query(
        "select assignmentSubmission from AssignmentSubmission assignmentSubmission where assignmentSubmission.gradedBy.login = ?#{authentication.name}"
    )
    List<AssignmentSubmission> findByGradedByIsCurrentUser();

    default Optional<AssignmentSubmission> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<AssignmentSubmission> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<AssignmentSubmission> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select assignmentSubmission from AssignmentSubmission assignmentSubmission left join fetch assignmentSubmission.student left join fetch assignmentSubmission.gradedBy",
        countQuery = "select count(assignmentSubmission) from AssignmentSubmission assignmentSubmission"
    )
    Page<AssignmentSubmission> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select assignmentSubmission from AssignmentSubmission assignmentSubmission left join fetch assignmentSubmission.student left join fetch assignmentSubmission.gradedBy"
    )
    List<AssignmentSubmission> findAllWithToOneRelationships();

    @Query(
        "select assignmentSubmission from AssignmentSubmission assignmentSubmission left join fetch assignmentSubmission.student left join fetch assignmentSubmission.gradedBy where assignmentSubmission.id =:id"
    )
    Optional<AssignmentSubmission> findOneWithToOneRelationships(@Param("id") Long id);
}
