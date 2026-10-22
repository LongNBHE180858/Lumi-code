package com.lumi.repository;

import com.lumi.domain.ProctorSession;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProctorSession entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProctorSessionRepository extends JpaRepository<ProctorSession, Long>, JpaSpecificationExecutor<ProctorSession> {}
