package com.lumi.repository;

import com.lumi.domain.AiGeneratedContent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AiGeneratedContent entity.
 */
@Repository
public interface AiGeneratedContentRepository
    extends JpaRepository<AiGeneratedContent, Long>, JpaSpecificationExecutor<AiGeneratedContent>
{
    @Query(
        "select aiGeneratedContent from AiGeneratedContent aiGeneratedContent where aiGeneratedContent.createdBy.login = ?#{authentication.name}"
    )
    List<AiGeneratedContent> findByCreatedByIsCurrentUser();

    @Query(
        "select aiGeneratedContent from AiGeneratedContent aiGeneratedContent where aiGeneratedContent.reviewedBy.login = ?#{authentication.name}"
    )
    List<AiGeneratedContent> findByReviewedByIsCurrentUser();

    default Optional<AiGeneratedContent> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<AiGeneratedContent> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<AiGeneratedContent> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select aiGeneratedContent from AiGeneratedContent aiGeneratedContent left join fetch aiGeneratedContent.createdBy left join fetch aiGeneratedContent.reviewedBy",
        countQuery = "select count(aiGeneratedContent) from AiGeneratedContent aiGeneratedContent"
    )
    Page<AiGeneratedContent> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select aiGeneratedContent from AiGeneratedContent aiGeneratedContent left join fetch aiGeneratedContent.createdBy left join fetch aiGeneratedContent.reviewedBy"
    )
    List<AiGeneratedContent> findAllWithToOneRelationships();

    @Query(
        "select aiGeneratedContent from AiGeneratedContent aiGeneratedContent left join fetch aiGeneratedContent.createdBy left join fetch aiGeneratedContent.reviewedBy where aiGeneratedContent.id =:id"
    )
    Optional<AiGeneratedContent> findOneWithToOneRelationships(@Param("id") Long id);
}
