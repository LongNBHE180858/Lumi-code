package com.lumi.repository;

import com.lumi.domain.FlashcardReview;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FlashcardReview entity.
 */
@Repository
public interface FlashcardReviewRepository extends JpaRepository<FlashcardReview, Long>, JpaSpecificationExecutor<FlashcardReview> {
    @Query("select flashcardReview from FlashcardReview flashcardReview where flashcardReview.student.login = ?#{authentication.name}")
    List<FlashcardReview> findByStudentIsCurrentUser();

    default Optional<FlashcardReview> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<FlashcardReview> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<FlashcardReview> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select flashcardReview from FlashcardReview flashcardReview left join fetch flashcardReview.student",
        countQuery = "select count(flashcardReview) from FlashcardReview flashcardReview"
    )
    Page<FlashcardReview> findAllWithToOneRelationships(Pageable pageable);

    @Query("select flashcardReview from FlashcardReview flashcardReview left join fetch flashcardReview.student")
    List<FlashcardReview> findAllWithToOneRelationships();

    @Query(
        "select flashcardReview from FlashcardReview flashcardReview left join fetch flashcardReview.student where flashcardReview.id =:id"
    )
    Optional<FlashcardReview> findOneWithToOneRelationships(@Param("id") Long id);
}
