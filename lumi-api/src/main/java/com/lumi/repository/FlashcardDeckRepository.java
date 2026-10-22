package com.lumi.repository;

import com.lumi.domain.FlashcardDeck;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FlashcardDeck entity.
 */
@Repository
public interface FlashcardDeckRepository extends JpaRepository<FlashcardDeck, Long>, JpaSpecificationExecutor<FlashcardDeck> {
    @Query("select flashcardDeck from FlashcardDeck flashcardDeck where flashcardDeck.owner.login = ?#{authentication.name}")
    List<FlashcardDeck> findByOwnerIsCurrentUser();

    default Optional<FlashcardDeck> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<FlashcardDeck> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<FlashcardDeck> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select flashcardDeck from FlashcardDeck flashcardDeck left join fetch flashcardDeck.owner left join fetch flashcardDeck.subject",
        countQuery = "select count(flashcardDeck) from FlashcardDeck flashcardDeck"
    )
    Page<FlashcardDeck> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select flashcardDeck from FlashcardDeck flashcardDeck left join fetch flashcardDeck.owner left join fetch flashcardDeck.subject"
    )
    List<FlashcardDeck> findAllWithToOneRelationships();

    @Query(
        "select flashcardDeck from FlashcardDeck flashcardDeck left join fetch flashcardDeck.owner left join fetch flashcardDeck.subject where flashcardDeck.id =:id"
    )
    Optional<FlashcardDeck> findOneWithToOneRelationships(@Param("id") Long id);
}
