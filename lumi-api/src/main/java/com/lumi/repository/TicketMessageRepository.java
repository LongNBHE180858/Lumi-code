package com.lumi.repository;

import com.lumi.domain.TicketMessage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TicketMessage entity.
 */
@Repository
public interface TicketMessageRepository extends JpaRepository<TicketMessage, Long>, JpaSpecificationExecutor<TicketMessage> {
    @Query("select ticketMessage from TicketMessage ticketMessage where ticketMessage.sender.login = ?#{authentication.name}")
    List<TicketMessage> findBySenderIsCurrentUser();

    @EntityGraph(attributePaths = "sender")
    Page<TicketMessage> findByTicketIdOrderByCreatedAtAscIdAsc(Long ticketId, Pageable pageable);

    default Optional<TicketMessage> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TicketMessage> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TicketMessage> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select ticketMessage from TicketMessage ticketMessage left join fetch ticketMessage.sender",
        countQuery = "select count(ticketMessage) from TicketMessage ticketMessage"
    )
    Page<TicketMessage> findAllWithToOneRelationships(Pageable pageable);

    @Query("select ticketMessage from TicketMessage ticketMessage left join fetch ticketMessage.sender")
    List<TicketMessage> findAllWithToOneRelationships();

    @Query("select ticketMessage from TicketMessage ticketMessage left join fetch ticketMessage.sender where ticketMessage.id =:id")
    Optional<TicketMessage> findOneWithToOneRelationships(@Param("id") Long id);
}
