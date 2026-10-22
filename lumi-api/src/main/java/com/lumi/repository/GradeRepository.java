package com.lumi.repository;

import com.lumi.domain.Grade;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Grade entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GradeRepository extends JpaRepository<Grade, Long>, JpaSpecificationExecutor<Grade> {
    java.util.List<Grade> findByActiveTrueOrderByLevelAsc();
    java.util.Optional<Grade> findByLevel(Integer level);
}
