package com.lumi.repository;

import com.lumi.domain.Assignment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Assignment entity.
 */
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long>, JpaSpecificationExecutor<Assignment> {
    default Optional<Assignment> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Assignment> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Assignment> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select assignment from Assignment assignment left join fetch assignment.studyClass",
        countQuery = "select count(assignment) from Assignment assignment"
    )
    Page<Assignment> findAllWithToOneRelationships(Pageable pageable);

    @Query("select assignment from Assignment assignment left join fetch assignment.studyClass")
    List<Assignment> findAllWithToOneRelationships();

    @Query("select assignment from Assignment assignment left join fetch assignment.studyClass where assignment.id =:id")
    Optional<Assignment> findOneWithToOneRelationships(@Param("id") Long id);
}
