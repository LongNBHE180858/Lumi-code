package com.lumi.repository;

import com.lumi.domain.CourseMaterial;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CourseMaterial entity.
 */
@Repository
public interface CourseMaterialRepository extends JpaRepository<CourseMaterial, Long>, JpaSpecificationExecutor<CourseMaterial> {
    List<CourseMaterial> findByLessonChapterCourseIdOrderByOrderIndexAscIdAsc(Long courseId);
    List<CourseMaterial> findByLessonIdOrderByOrderIndexAscIdAsc(Long lessonId);

    default Optional<CourseMaterial> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CourseMaterial> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CourseMaterial> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select courseMaterial from CourseMaterial courseMaterial left join fetch courseMaterial.lesson",
        countQuery = "select count(courseMaterial) from CourseMaterial courseMaterial"
    )
    Page<CourseMaterial> findAllWithToOneRelationships(Pageable pageable);

    @Query("select courseMaterial from CourseMaterial courseMaterial left join fetch courseMaterial.lesson")
    List<CourseMaterial> findAllWithToOneRelationships();

    @Query("select courseMaterial from CourseMaterial courseMaterial left join fetch courseMaterial.lesson where courseMaterial.id =:id")
    Optional<CourseMaterial> findOneWithToOneRelationships(@Param("id") Long id);
}
