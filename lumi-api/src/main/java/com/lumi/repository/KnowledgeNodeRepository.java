package com.lumi.repository;

import com.lumi.domain.KnowledgeNode;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the KnowledgeNode entity.
 */
@SuppressWarnings("unused")
@Repository
public interface KnowledgeNodeRepository extends JpaRepository<KnowledgeNode, Long>, JpaSpecificationExecutor<KnowledgeNode> {
    default Optional<KnowledgeNode> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<KnowledgeNode> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<KnowledgeNode> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select knowledgeNode from KnowledgeNode knowledgeNode left join fetch knowledgeNode.subject",
        countQuery = "select count(knowledgeNode) from KnowledgeNode knowledgeNode"
    )
    Page<KnowledgeNode> findAllWithToOneRelationships(Pageable pageable);

    @Query("select knowledgeNode from KnowledgeNode knowledgeNode left join fetch knowledgeNode.subject")
    List<KnowledgeNode> findAllWithToOneRelationships();

    @Query("select knowledgeNode from KnowledgeNode knowledgeNode left join fetch knowledgeNode.subject where knowledgeNode.id =:id")
    Optional<KnowledgeNode> findOneWithToOneRelationships(@Param("id") Long id);

    List<KnowledgeNode> findAllBySubjectId(Long subjectId);

    @Query(
        "select knowledgeNode from KnowledgeNode knowledgeNode left join fetch knowledgeNode.subject where knowledgeNode.subject.id = :subjectId"
    )
    List<KnowledgeNode> findAllWithSubjectBySubjectId(@Param("subjectId") Long subjectId);
}
