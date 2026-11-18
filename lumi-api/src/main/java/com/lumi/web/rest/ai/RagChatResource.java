package com.lumi.web.rest.ai;

import com.lumi.security.SecurityUtils;
import com.lumi.service.dto.ai.ChatRequest;
import com.lumi.service.ai.ChatMessageService;
import com.lumi.service.ai.ChatConversationService;
import com.lumi.service.ai.RagChatService;

import com.lumi.service.dto.ai.CreateConversationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
@ConditionalOnProperty(
    name = "lumi.ai.chat.enabled",
    havingValue = "true"
)
public class RagChatResource {

    private final RagChatService ragChatService;
    private final ChatConversationService conversationService;
    private final ChatMessageService messageService;

    @PostMapping("/chat")
    @PreAuthorize("isAuthenticated()")
    public RagChatService.ChatResult chat(
        @Valid @RequestBody ChatRequest request
    ) {
        String login = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Bạn cần đăng nhập."
            ));

        return ragChatService.chat(
            login,
            request.question().strip()
        );
    }

    @PostMapping("/conversations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatConversationService.CreatedConversation> createConversation(
        @Valid @RequestBody(required = false) CreateConversationRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(request == null || request.lessonId() == null ? conversationService.create() : conversationService.create(request.lessonId()));
    }

    @PostMapping("/conversations/{conversationId}/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatMessageService.SentMessage> sendMessage(
        @PathVariable("conversationId") Long conversationId,
        @Valid @RequestBody ChatRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(messageService.send(
                conversationId,
                request.question()
            ));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    @PreAuthorize("isAuthenticated()")
    public List<ChatMessageService.HistoryMessage> getMessages(
        @PathVariable("conversationId") Long conversationId
    ) {
        return messageService.history(conversationId);
    }
}
