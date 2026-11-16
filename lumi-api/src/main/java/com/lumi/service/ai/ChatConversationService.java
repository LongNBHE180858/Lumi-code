package com.lumi.service.ai;

import com.lumi.domain.AiConversation;
import com.lumi.domain.enumeration.AiConversationType;
import com.lumi.repository.AiConversationRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.SecurityUtils;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "lumi.ai.chat.enabled",
    havingValue = "true"
)
public class ChatConversationService {

    private final AiConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public record CreatedConversation(
        Long conversationId,
        Instant createdAt,
        Instant updatedAt
    ) {}

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public CreatedConversation create() {
        String login = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Bạn cần đăng nhập."
            ));

        var user = userRepository.findOneByLogin(login)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Không tìm thấy tài khoản."
            ));

        Instant now = Instant.now();

        AiConversation conversation = new AiConversation()
            .type(AiConversationType.RAG_TUTOR.name())
            .user(user)
            .createdAt(now)
            .updatedAt(now);

        conversation = conversationRepository.saveAndFlush(conversation);

        return new CreatedConversation(
            conversation.getId(),
            conversation.getCreatedAt(),
            conversation.getUpdatedAt()
        );
    }
}
