package com.lumi.repository;

import com.lumi.domain.ExamAnswer;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ExamAnswer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExamAnswerRepository extends JpaRepository<ExamAnswer, Long>, JpaSpecificationExecutor<ExamAnswer> {
    List<ExamAnswer> findByAttemptId(Long attemptId);
}
