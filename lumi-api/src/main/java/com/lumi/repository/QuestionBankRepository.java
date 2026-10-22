package com.lumi.repository;

import com.lumi.domain.QuestionBank;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the QuestionBank entity.
 */
@Repository
public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long>, JpaSpecificationExecutor<QuestionBank> {
    @Query("select questionBank from QuestionBank questionBank where questionBank.instructor.login = ?#{authentication.name}")
    List<QuestionBank> findByInstructorIsCurrentUser();

    default Optional<QuestionBank> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<QuestionBank> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<QuestionBank> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select questionBank from QuestionBank questionBank left join fetch questionBank.instructor",
        countQuery = "select count(questionBank) from QuestionBank questionBank"
    )
    Page<QuestionBank> findAllWithToOneRelationships(Pageable pageable);

    @Query("select questionBank from QuestionBank questionBank left join fetch questionBank.instructor")
    List<QuestionBank> findAllWithToOneRelationships();

    @Query("select questionBank from QuestionBank questionBank left join fetch questionBank.instructor where questionBank.id =:id")
    Optional<QuestionBank> findOneWithToOneRelationships(@Param("id") Long id);
}
