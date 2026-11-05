package com.lumi.repository;

import com.lumi.domain.QuestionKnowledgeLink;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the QuestionKnowledgeLink entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuestionKnowledgeLinkRepository
    extends JpaRepository<QuestionKnowledgeLink, Long>, JpaSpecificationExecutor<QuestionKnowledgeLink> {}
