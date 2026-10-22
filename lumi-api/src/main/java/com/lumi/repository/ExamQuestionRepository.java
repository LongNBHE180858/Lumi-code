package com.lumi.repository;

import com.lumi.domain.ExamQuestion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ExamQuestion entity.
 */
@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long>, JpaSpecificationExecutor<ExamQuestion> {
    default Optional<ExamQuestion> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ExamQuestion> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ExamQuestion> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select examQuestion from ExamQuestion examQuestion left join fetch examQuestion.exam",
        countQuery = "select count(examQuestion) from ExamQuestion examQuestion"
    )
    Page<ExamQuestion> findAllWithToOneRelationships(Pageable pageable);

    @Query("select examQuestion from ExamQuestion examQuestion left join fetch examQuestion.exam")
    List<ExamQuestion> findAllWithToOneRelationships();

    @Query("select examQuestion from ExamQuestion examQuestion left join fetch examQuestion.exam where examQuestion.id =:id")
    Optional<ExamQuestion> findOneWithToOneRelationships(@Param("id") Long id);
}
