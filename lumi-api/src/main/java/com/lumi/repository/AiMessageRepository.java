package com.lumi.repository;

import com.lumi.domain.AiMessage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the AiMessage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AiMessageRepository extends JpaRepository<AiMessage, Long>, JpaSpecificationExecutor<AiMessage> {
    java.util.Optional<AiMessage> findFirstByConversation_IdOrderByCreatedAtDescIdDesc(Long conversationId);

    @Query("""
        select m from AiMessage m where m.conversation.id = :id and m.conversation.user.login = :login
        and (cast(:beforeTime as timestamp) is null or m.createdAt < :beforeTime or (m.createdAt = :beforeTime and m.id < :beforeId))
        order by m.createdAt desc, m.id desc
        """)
    List<AiMessage> findMemoryBatch(@org.springframework.data.repository.query.Param("id") Long id,
        @org.springframework.data.repository.query.Param("login") String login,
        @org.springframework.data.repository.query.Param("beforeTime") java.time.Instant beforeTime,
        @org.springframework.data.repository.query.Param("beforeId") Long beforeId,
        org.springframework.data.domain.Pageable page);
    List<AiMessage> findByConversation_IdAndConversation_User_LoginAndConversation_TypeInOrderByCreatedAtAscIdAsc(
        Long conversationId,
        String login,
        List<String> types
    );
}
