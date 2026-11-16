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
    List<AiMessage> findByConversation_IdAndConversation_User_LoginAndConversation_TypeInOrderByCreatedAtAscIdAsc(
        Long conversationId,
        String login,
        List<String> types
    );
}
