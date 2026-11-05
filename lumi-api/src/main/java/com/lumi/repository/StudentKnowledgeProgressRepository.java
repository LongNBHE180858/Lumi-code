package com.lumi.repository;

import com.lumi.domain.StudentKnowledgeProgress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StudentKnowledgeProgress entity.
 */
@Repository
public interface StudentKnowledgeProgressRepository
    extends JpaRepository<StudentKnowledgeProgress, Long>, JpaSpecificationExecutor<StudentKnowledgeProgress>
{
    @Query(
        "select studentKnowledgeProgress from StudentKnowledgeProgress studentKnowledgeProgress where studentKnowledgeProgress.student.login = ?#{authentication.name}"
    )
    List<StudentKnowledgeProgress> findByStudentIsCurrentUser();

    default Optional<StudentKnowledgeProgress> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<StudentKnowledgeProgress> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<StudentKnowledgeProgress> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select studentKnowledgeProgress from StudentKnowledgeProgress studentKnowledgeProgress left join fetch studentKnowledgeProgress.student",
        countQuery = "select count(studentKnowledgeProgress) from StudentKnowledgeProgress studentKnowledgeProgress"
    )
    Page<StudentKnowledgeProgress> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select studentKnowledgeProgress from StudentKnowledgeProgress studentKnowledgeProgress left join fetch studentKnowledgeProgress.student"
    )
    List<StudentKnowledgeProgress> findAllWithToOneRelationships();

    @Query(
        "select studentKnowledgeProgress from StudentKnowledgeProgress studentKnowledgeProgress left join fetch studentKnowledgeProgress.student where studentKnowledgeProgress.id =:id"
    )
    Optional<StudentKnowledgeProgress> findOneWithToOneRelationships(@Param("id") Long id);
}
