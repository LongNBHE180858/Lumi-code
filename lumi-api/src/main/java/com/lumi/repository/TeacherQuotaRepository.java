package com.lumi.repository;

import com.lumi.domain.TeacherQuota;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TeacherQuota entity.
 */
@Repository
public interface TeacherQuotaRepository extends JpaRepository<TeacherQuota, Long>, JpaSpecificationExecutor<TeacherQuota> {
    default Optional<TeacherQuota> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TeacherQuota> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TeacherQuota> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select teacherQuota from TeacherQuota teacherQuota left join fetch teacherQuota.user left join fetch teacherQuota.currentPlan",
        countQuery = "select count(teacherQuota) from TeacherQuota teacherQuota"
    )
    Page<TeacherQuota> findAllWithToOneRelationships(Pageable pageable);

    @Query("select teacherQuota from TeacherQuota teacherQuota left join fetch teacherQuota.user left join fetch teacherQuota.currentPlan")
    List<TeacherQuota> findAllWithToOneRelationships();

    @Query(
        "select teacherQuota from TeacherQuota teacherQuota left join fetch teacherQuota.user left join fetch teacherQuota.currentPlan where teacherQuota.id =:id"
    )
    Optional<TeacherQuota> findOneWithToOneRelationships(@Param("id") Long id);
}
