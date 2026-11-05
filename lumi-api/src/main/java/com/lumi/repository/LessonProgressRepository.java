package com.lumi.repository;

import com.lumi.domain.LessonProgress;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LessonProgress entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long>, JpaSpecificationExecutor<LessonProgress> {}
