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
        if (ownerLogin == null || ownerLogin.isBlank()
            || question == null || question.isBlank()) {
            throw new IllegalArgumentException("Thiếu login hoặc câu hỏi.");
        }

        // 1. Tạo embedding cho câu hỏi.
        var options = GoogleGenAiTextEmbeddingOptions.builder()
            .model("gemini-embedding-001")
            .dimensions(768)
            .taskType(
                GoogleGenAiTextEmbeddingOptions.TaskType.RETRIEVAL_QUERY
            )
            .build();

        var response = embeddingModel.call(new EmbeddingRequest(List.of(question), options));

        if (response.getResults().size() != 1) {
            throw new IllegalStateException("Không nhận được một embedding.");
        }

        float[] vector = response.getResults().get(0).getOutput();
        validateVector(vector);

        // 2. Tìm đoạn gần nhất trong tài liệu demo của người dùng.
        var matches = chunkRepository.findNearestChunks(
            ownerLogin,
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
        var generated = answerService.answer(
            question,
            context.toString()
        );

        if (!generated.supported()) {
            return insufficientContext();
        }

        // 5. Kiểm tra số nguồn model trả về trước khi ánh xạ sang ID thật.
        List<Citation> citations = new ArrayList<>();

        for (Integer number : generated.sourceNumbers().stream().distinct().toList()) {
            if (number == null || number < 1 || number > matches.size()) {
                throw new IllegalStateException(
                    "Model trả số nguồn không hợp lệ: " + number
                );
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
            throw new IllegalStateException("Vector phải có đúng 768 chiều.");
        }

        double squaredNorm = 0;

        for (float value : vector) {
            if (!Float.isFinite(value)) {
                throw new IllegalStateException("Vector chứa số không hợp lệ.");
            }
            squaredNorm += (double) value * value;
        }

        if (squaredNorm == 0) {
            throw new IllegalStateException("Vector không được toàn số 0.");
        }
    }
}
