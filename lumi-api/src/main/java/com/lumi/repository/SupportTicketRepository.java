package com.lumi.repository;

import com.lumi.domain.SupportTicket;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SupportTicket entity.
 */
@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long>, JpaSpecificationExecutor<SupportTicket> {
    @Query("select supportTicket from SupportTicket supportTicket where supportTicket.user.login = ?#{authentication.name}")
    List<SupportTicket> findByUserIsCurrentUser();

    @EntityGraph(attributePaths = "user")
    Page<SupportTicket> findByUserId(Long userId, Pageable pageable);

    @Query(
        value = "select ticket from SupportTicket ticket join fetch ticket.user user where (:status is null or ticket.status = :status) and (:type is null or ticket.type = :type) and (:search is null or lower(ticket.subject) like :search or lower(user.login) like :search)",
        countQuery = "select count(ticket) from SupportTicket ticket join ticket.user user where (:status is null or ticket.status = :status) and (:type is null or ticket.type = :type) and (:search is null or lower(ticket.subject) like :search or lower(user.login) like :search)"
    )
    Page<SupportTicket> findAdminPage(
        @Param("status") String status,
        @Param("type") String type,
        @Param("search") String search,
        Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ticket from SupportTicket ticket join fetch ticket.user where ticket.id = :id")
    Optional<SupportTicket> findByIdForUpdate(@Param("id") Long id);

    default Optional<SupportTicket> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SupportTicket> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SupportTicket> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select supportTicket from SupportTicket supportTicket left join fetch supportTicket.user",
        countQuery = "select count(supportTicket) from SupportTicket supportTicket"
    )
    Page<SupportTicket> findAllWithToOneRelationships(Pageable pageable);

    @Query("select supportTicket from SupportTicket supportTicket left join fetch supportTicket.user")
    List<SupportTicket> findAllWithToOneRelationships();

    @Query("select supportTicket from SupportTicket supportTicket left join fetch supportTicket.user where supportTicket.id =:id")
    Optional<SupportTicket> findOneWithToOneRelationships(@Param("id") Long id);
}
