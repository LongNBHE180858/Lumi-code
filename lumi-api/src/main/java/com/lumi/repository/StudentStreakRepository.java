package com.lumi.repository;

import com.lumi.domain.StudentStreak;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StudentStreak entity.
 */
@Repository
public interface StudentStreakRepository
        extends JpaRepository<StudentStreak, Long>, JpaSpecificationExecutor<StudentStreak> {
    default Optional<StudentStreak> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<StudentStreak> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<StudentStreak> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(value = "select studentStreak from StudentStreak studentStreak left join fetch studentStreak.user", countQuery = "select count(studentStreak) from StudentStreak studentStreak")
    Page<StudentStreak> findAllWithToOneRelationships(Pageable pageable);

    @Query("select studentStreak from StudentStreak studentStreak left join fetch studentStreak.user")
    List<StudentStreak> findAllWithToOneRelationships();

    @Query("select studentStreak from StudentStreak studentStreak left join fetch studentStreak.user where studentStreak.id =:id")
    Optional<StudentStreak> findOneWithToOneRelationships(@Param("id") Long id);

    Optional<StudentStreak> findByUserId(Long userId);
}
