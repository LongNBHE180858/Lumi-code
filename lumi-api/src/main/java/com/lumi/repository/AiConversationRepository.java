package com.lumi.repository;

import com.lumi.domain.AiConversation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AiConversation entity.
 */
@Repository
public interface AiConversationRepository extends JpaRepository<AiConversation, Long>, JpaSpecificationExecutor<AiConversation> {
    @Query("select aiConversation from AiConversation aiConversation where aiConversation.user.login = ?#{authentication.name}")
    List<AiConversation> findByUserIsCurrentUser();

    default Optional<AiConversation> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<AiConversation> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<AiConversation> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select aiConversation from AiConversation aiConversation left join fetch aiConversation.user left join fetch aiConversation.lesson",
        countQuery = "select count(aiConversation) from AiConversation aiConversation"
    )
    Page<AiConversation> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select aiConversation from AiConversation aiConversation left join fetch aiConversation.user left join fetch aiConversation.lesson"
    )
    List<AiConversation> findAllWithToOneRelationships();

    @Query(
        "select aiConversation from AiConversation aiConversation left join fetch aiConversation.user left join fetch aiConversation.lesson where aiConversation.id =:id"
    )
    Optional<AiConversation> findOneWithToOneRelationships(@Param("id") Long id);
}
