package com.lumi.service.ai;

import com.lumi.domain.Lesson;
import com.lumi.repository.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ChatContextService {
    private final AiConversationRepository conversations;
    private final AiMessageRepository messages;
    private final LessonRepository lessons;
    public record Context(Long lessonId, Long lastMessageId, String lesson, String history) {}

    @Transactional(readOnly = true)
    public Lesson requireLesson(Long id, String login) {
        if (id == null) return null;
        if (id <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bài học không hợp lệ.");
        return lessons.findAccessibleForAi(id, login).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài học."));
    }
//lấy ngữ cảnh hội thoại tại thời điểm người dùng gửi tin nhắn.
    @Transactional(readOnly = true)
    public Context snapshot(Long id, String login, String question) {
        int budget = 3000 - words(question);
        if (budget < 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Câu hỏi vượt giới hạn 3.000 từ.");
        var conversation = conversations.findByIdAndUser_Login(id, login).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hội thoại."));
        Long lessonId = conversation.getLesson() == null ? null : conversation.getLesson().getId();
        Lesson lesson = requireLesson(lessonId, login);
        String description = "";
        if (lesson != null) {
            var course = lesson.getChapter().getCourse();
            description = "Bài: " + lesson.getTitle() + "; Chương: " + lesson.getChapter().getTitle()
                + "; Khóa: " + course.getTitle()
                + "; Môn: " + (course.getSubject() == null ? "" : course.getSubject().getName())
                + "; Lớp: " + (course.getGrade() == null ? "" : course.getGrade().getLevel());
        }
        List<String> pairs = new ArrayList<>();
        Long lastId = null;
        com.lumi.domain.AiMessage assistant = null;
        boolean stop = false;
        // Keyset pagination avoids loading an unbounded history and shifting offsets.
        java.time.Instant beforeTime = null;
        Long beforeId = null;
        while (!stop) {
            var batch = messages.findMemoryBatch(id, login, beforeTime, beforeId, PageRequest.of(0, 32));
            if (batch.isEmpty()) break;
            if (lastId == null) lastId = batch.get(0).getId();
            for (var message : batch) {
                if ("ASSISTANT".equals(message.getSenderRole())) {
                    assistant = message;
                } else if ("USER".equals(message.getSenderRole()) && assistant != null) {
                    String status = assistant.getGenerationStatus();
                    String content = assistant.getContent();
                    if (com.lumi.domain.enumeration.AiMessageGenerationStatus.INSUFFICIENT_CONTEXT.name().equals(status)) {
                        content = "Lượt này chưa tìm thấy đủ thông tin trong các nguồn truy hồi để trả lời.";
                    }
                    String pair = "USER: " + message.getContent() + "\nASSISTANT_STATUS: " + status
                        + "\nASSISTANT: " + content;
                    int size = words(pair);
                    if (size > budget) { stop = true; break; }
                    budget -= size;
                    pairs.add(pair);
                    assistant = null;
                }
            }
            var last = batch.get(batch.size() - 1);
            beforeTime = last.getCreatedAt();
            beforeId = last.getId();
            if (batch.size() < 32 || beforeTime == null) break;
        }
        Collections.reverse(pairs);
        return new Context(lessonId, lastId, description, String.join("\n\n", pairs));
    }

    public static int words(String text) {
        return text == null || text.isBlank() ? 0 : text.strip().split("(?U)\\s+").length;
    }
}
