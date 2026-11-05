package com.lumi.repository;

import com.lumi.domain.LearningDocument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LearningDocument entity.
 */
@Repository
public interface LearningDocumentRepository extends JpaRepository<LearningDocument, Long>, JpaSpecificationExecutor<LearningDocument> {
    @Query(
        "select learningDocument from LearningDocument learningDocument where learningDocument.uploadedBy.login = ?#{authentication.name}"
    )
    List<LearningDocument> findByUploadedByIsCurrentUser();

    default Optional<LearningDocument> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<LearningDocument> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<LearningDocument> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select learningDocument from LearningDocument learningDocument left join fetch learningDocument.uploadedBy",
        countQuery = "select count(learningDocument) from LearningDocument learningDocument"
    )
    Page<LearningDocument> findAllWithToOneRelationships(Pageable pageable);

    @Query("select learningDocument from LearningDocument learningDocument left join fetch learningDocument.uploadedBy")
    List<LearningDocument> findAllWithToOneRelationships();

    @Query(
        "select learningDocument from LearningDocument learningDocument left join fetch learningDocument.uploadedBy where learningDocument.id =:id"
    )
    Optional<LearningDocument> findOneWithToOneRelationships(@Param("id") Long id);
}
