package com.lumi.repository;

import com.lumi.domain.AuditLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AuditLog entity.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {
    @Query("select auditLog from AuditLog auditLog where auditLog.actor.login = ?#{authentication.name}")
    List<AuditLog> findByActorIsCurrentUser();

    default Optional<AuditLog> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<AuditLog> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<AuditLog> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select auditLog from AuditLog auditLog left join fetch auditLog.actor",
        countQuery = "select count(auditLog) from AuditLog auditLog"
    )
    Page<AuditLog> findAllWithToOneRelationships(Pageable pageable);

    @Query("select auditLog from AuditLog auditLog left join fetch auditLog.actor")
    List<AuditLog> findAllWithToOneRelationships();

    @Query("select auditLog from AuditLog auditLog left join fetch auditLog.actor where auditLog.id =:id")
    Optional<AuditLog> findOneWithToOneRelationships(@Param("id") Long id);
}
