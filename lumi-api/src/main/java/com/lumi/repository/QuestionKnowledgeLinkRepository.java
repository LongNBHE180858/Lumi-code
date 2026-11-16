package com.lumi.repository;

import com.lumi.domain.QuestionKnowledgeLink;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the QuestionKnowledgeLink entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuestionKnowledgeLinkRepository
    extends JpaRepository<QuestionKnowledgeLink, Long>, JpaSpecificationExecutor<QuestionKnowledgeLink>
{
    List<QuestionKnowledgeLink> findByQuestionId(Long questionId);

    Optional<QuestionKnowledgeLink> findFirstByQuestionId(Long questionId);

    @Query("select link from QuestionKnowledgeLink link where link.question.bank.id = :bankId")
    List<QuestionKnowledgeLink> findByBankId(@Param("bankId") Long bankId);
}
