package com.lumi.repository;

import com.lumi.domain.AiMessage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AiMessage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AiMessageRepository extends JpaRepository<AiMessage, Long>, JpaSpecificationExecutor<AiMessage> {}
