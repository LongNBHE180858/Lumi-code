package com.lumi.repository;

import com.lumi.domain.Refund;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Refund entity.
 */
@Repository
public interface RefundRepository extends JpaRepository<Refund, Long>, JpaSpecificationExecutor<Refund> {
    @Query("select refund from Refund refund where refund.processedBy.login = ?#{authentication.name}")
    List<Refund> findByProcessedByIsCurrentUser();

    default Optional<Refund> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Refund> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Refund> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select refund from Refund refund left join fetch refund.processedBy",
        countQuery = "select count(refund) from Refund refund"
    )
    Page<Refund> findAllWithToOneRelationships(Pageable pageable);

    @Query("select refund from Refund refund left join fetch refund.processedBy")
    List<Refund> findAllWithToOneRelationships();

    @Query("select refund from Refund refund left join fetch refund.processedBy where refund.id =:id")
    Optional<Refund> findOneWithToOneRelationships(@Param("id") Long id);
}
