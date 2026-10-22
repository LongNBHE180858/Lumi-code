package com.lumi.repository;

import com.lumi.domain.SystemPlan;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SystemPlan entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SystemPlanRepository extends JpaRepository<SystemPlan, Long>, JpaSpecificationExecutor<SystemPlan> {}
