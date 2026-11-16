package com.lumi.repository;

import com.lumi.domain.QuestionOption;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the QuestionOption entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long>, JpaSpecificationExecutor<QuestionOption> {
    List<QuestionOption> findByQuestionIdOrderByOrderIndexAsc(Long questionId);

    void deleteByQuestionId(Long questionId);
}
