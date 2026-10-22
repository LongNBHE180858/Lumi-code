package com.lumi.repository;

import com.lumi.domain.Flashcard;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Flashcard entity.
 */
@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Long>, JpaSpecificationExecutor<Flashcard> {
    default Optional<Flashcard> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Flashcard> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Flashcard> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select flashcard from Flashcard flashcard left join fetch flashcard.deck",
        countQuery = "select count(flashcard) from Flashcard flashcard"
    )
    Page<Flashcard> findAllWithToOneRelationships(Pageable pageable);

    @Query("select flashcard from Flashcard flashcard left join fetch flashcard.deck")
    List<Flashcard> findAllWithToOneRelationships();

    @Query("select flashcard from Flashcard flashcard left join fetch flashcard.deck where flashcard.id =:id")
    Optional<Flashcard> findOneWithToOneRelationships(@Param("id") Long id);
}
