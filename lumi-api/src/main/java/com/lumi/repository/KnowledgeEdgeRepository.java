package com.lumi.repository;

import com.lumi.domain.KnowledgeEdge;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the KnowledgeEdge entity.
 */
@SuppressWarnings("unused")
@Repository
public interface KnowledgeEdgeRepository extends JpaRepository<KnowledgeEdge, Long>, JpaSpecificationExecutor<KnowledgeEdge> {
    default Optional<KnowledgeEdge> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<KnowledgeEdge> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<KnowledgeEdge> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select knowledgeEdge from KnowledgeEdge knowledgeEdge left join fetch knowledgeEdge.fromNode left join fetch knowledgeEdge.toNode",
        countQuery = "select count(knowledgeEdge) from KnowledgeEdge knowledgeEdge"
    )
    Page<KnowledgeEdge> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select knowledgeEdge from KnowledgeEdge knowledgeEdge left join fetch knowledgeEdge.fromNode left join fetch knowledgeEdge.toNode"
    )
    List<KnowledgeEdge> findAllWithToOneRelationships();

    @Query(
        "select knowledgeEdge from KnowledgeEdge knowledgeEdge left join fetch knowledgeEdge.fromNode left join fetch knowledgeEdge.toNode where knowledgeEdge.id =:id"
    )
    Optional<KnowledgeEdge> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        "select e from KnowledgeEdge e " +
            "left join fetch e.fromNode fn " +
            "left join fetch e.toNode tn " +
            "where fn.subject.id = :subjectId and tn.subject.id = :subjectId"
    )
    List<KnowledgeEdge> findAllBySubjectId(@Param("subjectId") Long subjectId);

    @Query("select e from KnowledgeEdge e " + "where e.fromNode.subject.id = :subjectId or e.toNode.subject.id = :subjectId")
    List<KnowledgeEdge> findAllReferencingSubjectId(@Param("subjectId") Long subjectId);
}
