package com.lumi.service.ai;

import com.lumi.domain.AiMessage;
import com.lumi.domain.enumeration.AiConversationType;
import com.lumi.domain.enumeration.AiMessageGenerationStatus;
import com.lumi.domain.enumeration.AiMessageSenderRole;
import com.lumi.repository.AiConversationRepository;
import com.lumi.repository.AiMessageRepository;
import com.lumi.security.SecurityUtils;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "lumi.ai.chat.enabled",
    havingValue = "true"
)
public class ChatMessageService {

    private static final List<String> SUPPORTED_CONVERSATION_TYPES = List.of(
        AiConversationType.RAG_TUTOR.name(), AiConversationType.DEMO.name()
    );

    private final AiConversationRepository conversationRepository;
    private final AiMessageRepository messageRepository;
    private final RagChatService ragChatService;
    private final PlatformTransactionManager transactionManager;
    private final ChatContextService contexts;

    @Value("${spring.ai.openai.chat.model}")
    private String chatModelName;

    public record SentMessage(
        Long conversationId,
        Long userMessageId,
        Long assistantMessageId,
        RagChatService.ChatResult result
    ) {}

    @PreAuthorize("isAuthenticated()")
    public SentMessage send(Long conversationId, String question) {
        // không tìm thấy hội thoại hoặc không phải chủ sở hữu đều trả 404, tránh tiết lộ hội thoại của tài khoản khác.
        if (conversationId == null || conversationId <= 0
            || question == null || question.isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Hội thoại hoặc câu hỏi không hợp lệ."
            );
        }

        String login = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập."
            ));

        // 1. Không gọi AI nếu hội thoại không thuộc người đăng nhập.
        boolean ownsConversation = conversationRepository.existsByIdAndUser_LoginAndTypeIn(
            conversationId, login, SUPPORTED_CONVERSATION_TYPES
        );
        if (!ownsConversation) {
            throw conversationNotFound();
        }

        String normalizedQuestion = question.strip(); //bỏ khoảng trắng
        Instant questionTime = Instant.now();

        // 2. Gọi Gemini ngoài transaction ghi DB.
        var snapshot = contexts.snapshot(conversationId, login, normalizedQuestion);
        var result = snapshot.lesson().isBlank() && snapshot.history().isBlank()
            ? ragChatService.chat(login, normalizedQuestion)
            : ragChatService.chat(login, normalizedQuestion, snapshot);

        // 3. Chỉ mở transaction khi đã có kết quả RAG.
        return new TransactionTemplate(transactionManager).execute(tx -> {

            var conversation = conversationRepository
                .findByIdAndUser_LoginAndTypeIn(conversationId, login, SUPPORTED_CONVERSATION_TYPES)
                .orElseThrow(this::conversationNotFound);

            Long currentLast = messageRepository.findFirstByConversation_IdOrderByCreatedAtDescIdDesc(conversationId)
                .map(AiMessage::getId).orElse(null);
            Long currentLesson = conversation.getLesson() == null ? null : conversation.getLesson().getId();
            if (!java.util.Objects.equals(currentLast, snapshot.lastMessageId())
                || !java.util.Objects.equals(currentLesson, snapshot.lessonId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Hội thoại đã thay đổi. Vui lòng gửi lại.");
            }
            contexts.requireLesson(currentLesson, login);
            Instant now = Instant.now();

            AiMessage userMessage = new AiMessage()
                .conversation(conversation)
                .senderRole(AiMessageSenderRole.USER.name())
                .content(normalizedQuestion)
                .createdAt(questionTime)
                .citations(List.of())
                .generationStatus(AiMessageGenerationStatus.NOT_APPLICABLE.name());

            // Lưu các nguồn đã chọn vào cột citations kiểu jsonb.
            List<Map<String, Object>> citations = result.citations()
                .stream()
                .map(source -> {
                    Map<String, Object> citation = new LinkedHashMap<>();

                    citation.put("sourceNumber", source.sourceNumber());
                    citation.put("documentId", source.documentId());
                    citation.put("chunkId", source.chunkId());
                    citation.put("documentTitle", source.documentTitle());
                    citation.put("pageNumber", source.pageNumber());

                    return citation;
                })
                .toList();

            AiMessage assistantMessage = new AiMessage()
                .conversation(conversation)
                .senderRole(AiMessageSenderRole.ASSISTANT.name())
                .content(result.answer())
                .createdAt(now)
                .citations(citations)
                .generationStatus(result.status().name())
                .modelName(chatModelName);

            userMessage = messageRepository.save(userMessage);
            assistantMessage = messageRepository.save(assistantMessage);

            conversation.setUpdatedAt(now);
            messageRepository.flush();

            return new SentMessage(
                conversationId,
                userMessage.getId(),
                assistantMessage.getId(),
                result
            );
        });
    }

    private ResponseStatusException conversationNotFound() {
        return new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Không tìm thấy hội thoại."
        );
    }

    public record HistoryMessage(
        Long id,
        String senderRole,
        String content,
        List<Map<String, Object>> citations,
        Instant createdAt,
        String generationStatus
    ) {}

    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public List<HistoryMessage> history(Long conversationId) {
        if (conversationId == null || conversationId <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "ID hội thoại không hợp lệ."
            );
        }

        String login = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Bạn cần đăng nhập."
            ));

        // Phân biệt hội thoại chưa có tin nhắn với hội thoại không được truy cập.
        if (!conversationRepository.existsByIdAndUser_LoginAndTypeIn(conversationId, login, SUPPORTED_CONVERSATION_TYPES)) {
            throw conversationNotFound();
        }

        return messageRepository
            .findByConversation_IdAndConversation_User_LoginAndConversation_TypeInOrderByCreatedAtAscIdAsc(
                conversationId, login, SUPPORTED_CONVERSATION_TYPES
            )
            .stream()
            .map(message -> new HistoryMessage(
                message.getId(),
                message.getSenderRole(),
                message.getContent(),
                message.getCitations(),
                message.getCreatedAt(),
                message.getGenerationStatus()
            ))
            .toList();
    }
}
