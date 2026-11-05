package com.lumi.repository;

import com.lumi.domain.TeacherVerification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TeacherVerification entity.
 */
@Repository
public interface TeacherVerificationRepository
    extends JpaRepository<TeacherVerification, Long>, JpaSpecificationExecutor<TeacherVerification>
{
    @Query(
        "select teacherVerification from TeacherVerification teacherVerification where teacherVerification.user.login = ?#{authentication.name}"
    )
    List<TeacherVerification> findByUserIsCurrentUser();

    @Query(
        "select teacherVerification from TeacherVerification teacherVerification where teacherVerification.reviewedBy.login = ?#{authentication.name}"
    )
    List<TeacherVerification> findByReviewedByIsCurrentUser();

    default Optional<TeacherVerification> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TeacherVerification> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TeacherVerification> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select teacherVerification from TeacherVerification teacherVerification left join fetch teacherVerification.user left join fetch teacherVerification.reviewedBy",
        countQuery = "select count(teacherVerification) from TeacherVerification teacherVerification"
    )
    Page<TeacherVerification> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select teacherVerification from TeacherVerification teacherVerification left join fetch teacherVerification.user left join fetch teacherVerification.reviewedBy"
    )
    List<TeacherVerification> findAllWithToOneRelationships();

    @Query(
        "select teacherVerification from TeacherVerification teacherVerification left join fetch teacherVerification.user left join fetch teacherVerification.reviewedBy where teacherVerification.id =:id"
    )
    Optional<TeacherVerification> findOneWithToOneRelationships(@Param("id") Long id);

    Optional<TeacherVerification> findOneByUserId(Long userId);

    Optional<TeacherVerification> findOneByUserLogin(String login);
}
