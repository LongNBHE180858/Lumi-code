package com.lumi.repository;

import com.lumi.domain.AssignmentStudent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AssignmentStudent entity.
 */
@Repository
public interface AssignmentStudentRepository extends JpaRepository<AssignmentStudent, Long>, JpaSpecificationExecutor<AssignmentStudent> {
    @Query(
        "select assignmentStudent from AssignmentStudent assignmentStudent where assignmentStudent.student.login = ?#{authentication.name}"
    )
    List<AssignmentStudent> findByStudentIsCurrentUser();

    default Optional<AssignmentStudent> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<AssignmentStudent> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<AssignmentStudent> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select assignmentStudent from AssignmentStudent assignmentStudent left join fetch assignmentStudent.student",
        countQuery = "select count(assignmentStudent) from AssignmentStudent assignmentStudent"
    )
    Page<AssignmentStudent> findAllWithToOneRelationships(Pageable pageable);

    @Query("select assignmentStudent from AssignmentStudent assignmentStudent left join fetch assignmentStudent.student")
    List<AssignmentStudent> findAllWithToOneRelationships();

    @Query(
        "select assignmentStudent from AssignmentStudent assignmentStudent left join fetch assignmentStudent.student where assignmentStudent.id =:id"
    )
    Optional<AssignmentStudent> findOneWithToOneRelationships(@Param("id") Long id);
}
