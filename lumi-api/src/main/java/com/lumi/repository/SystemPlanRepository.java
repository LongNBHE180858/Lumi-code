package com.lumi.repository;

import com.lumi.domain.SystemPlan;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SystemPlan entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SystemPlanRepository extends JpaRepository<SystemPlan, Long> {
    Page<SystemPlan> findByStatusNot(String status, Pageable pageable);

    long countByPackageKey(String packageKey);

    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select plan from SystemPlan plan where plan.id = :id")
    Optional<SystemPlan> findByIdForUpdate(@Param("id") Long id);
}
