package com.lumi.service.ai;

import com.lumi.domain.enumeration.AiMessageGenerationStatus;
import com.lumi.repository.PdfChunkRepository;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.google.genai.text.GoogleGenAiTextEmbeddingOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "lumi.ai.chat.enabled",
    havingValue = "true"
)
public class RagChatService {
    private static final Logger LOG =
        LoggerFactory.getLogger(RagChatService.class);

    private final EmbeddingModel embeddingModel;
    private final PdfChunkRepository chunkRepository;
    private final RagAnswerService answerService;
    private final QuestionContextService questionContexts;

    // Một nguồn trích dẫn trong kết quả trả về.
    public record Citation(
        int sourceNumber,
        Long documentId,
        Long chunkId,
        String documentTitle,
        Integer pageNumber
    ) {}

    public record ChatResult(
        AiMessageGenerationStatus status,
        String answer,
        List<Citation> citations
    ) {}

    public ChatResult chat(String ownerLogin, String question) {
        return chat(ownerLogin, question, new ChatContextService.Context(null, null, "", ""));
    }

    public ChatResult chat(String ownerLogin, String question, ChatContextService.Context memory) {
        if (ownerLogin == null || ownerLogin.isBlank()
            || question == null || question.isBlank()) {
            throw new IllegalArgumentException("Thiếu login hoặc câu hỏi.");
        }

        var routed = questionContexts.resolve(question, memory);
        if (routed.type() != QuestionContextService.QuestionType.KNOWLEDGE) {
            var status = routed.type() == QuestionContextService.QuestionType.SOCIAL
                ? AiMessageGenerationStatus.SOCIAL : AiMessageGenerationStatus.CLARIFICATION;
            return new ChatResult(status, routed.reply(), List.of());
        }
        String retrievalQuestion = routed.question();

        // 1. Tạo embedding cho câu hỏi.
        var options = GoogleGenAiTextEmbeddingOptions.builder()
            .model("gemini-embedding-001")
            .dimensions(768)
            .taskType(
                GoogleGenAiTextEmbeddingOptions.TaskType.RETRIEVAL_QUERY
            )
            .build();

        EmbeddingResponse response;
        try {
            response = embeddingModel.call(new EmbeddingRequest(List.of(retrievalQuestion), options));
        } catch (RuntimeException failure) {
            throw AiChatException.providerFailure(failure);
        }

        if (response == null || response.getResults() == null || response.getResults().size() != 1
            || response.getResults().get(0) == null) {
            throw AiChatException.invalidResponse();
        }

        float[] vector = response.getResults().get(0).getOutput();
        validateVector(vector);

        // 2. Tìm top 3 trên kho nguồn AI dùng chung, không lọc người nạp.
        var matches = chunkRepository.findNearestChunks(
            Arrays.toString(vector)
        );

        if (matches.isEmpty()) {
            return insufficientContext();
        }

        // 3. Ghép các chunk thành context, giữ thứ tự retrieval.
        // Số nguồn bắt đầu từ 1 để model dễ tham chiếu.
        StringBuilder context = new StringBuilder();

        for (int i = 0; i < matches.size(); i++) {
            var source = matches.get(i);
            int sourceNumber = i + 1;

            context.append("""
        [NGUỒN %d]
        %s
        [KẾT THÚC NGUỒN %d]

        """.formatted(
                sourceNumber,
                source.getChunkText(),
                sourceNumber
            ));

            LOG.info(
                "RAG SOURCE: number={}, documentId={}, chunkId={}, distance={}",
                sourceNumber,
                source.getDocumentId(),
                source.getChunkId(),
                source.getDistance()
            );
        }

        // 4. Model đọc toàn bộ context và chọn các nguồn đã sử dụng.
        var generated = memory.lesson().isBlank() && memory.history().isBlank()
            ? answerService.answer(question, context.toString())
            : answerService.answer(question, context.toString(), memory, retrievalQuestion);

        if (!generated.supported()) {
            return insufficientContext();
        }

        // 5. Kiểm tra số nguồn model trả về trước khi ánh xạ sang ID thật.
        List<Citation> citations = new ArrayList<>();

        for (Integer number : generated.sourceNumbers().stream().distinct().toList()) {
            if (number == null || number < 1 || number > matches.size()) {
                throw AiChatException.invalidResponse();
            }

            var source = matches.get(number - 1);

            citations.add(new Citation(
                number,
                source.getDocumentId(),
                source.getChunkId(),
                source.getDocumentTitle(),
                source.getPageNumber()
            ));
        }

        return new ChatResult(
            AiMessageGenerationStatus.ANSWERED,
            generated.answer(),
            List.copyOf(citations)
        );
    }

    private ChatResult insufficientContext() {
        return new ChatResult(
            AiMessageGenerationStatus.INSUFFICIENT_CONTEXT,
            "",
            List.of()
        );
    }

    private void validateVector(float[] vector) {
        if (vector == null || vector.length != 768) {
            throw AiChatException.invalidResponse();
        }

        double squaredNorm = 0;

        for (float value : vector) {
            if (!Float.isFinite(value)) {
                throw AiChatException.invalidResponse();
            }
            squaredNorm += (double) value * value;
        }

        if (squaredNorm == 0) {
            throw AiChatException.invalidResponse();
        }
    }
}
