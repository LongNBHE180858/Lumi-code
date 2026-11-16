package com.lumi.service.ai.demo;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "lumi.ai.demo-embedding", havingValue = "true")
public class DemoEmbeddingRunner implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DemoEmbeddingRunner.class);

    private final EmbeddingModel embeddingModel;
    private final ObjectMapper objectMapper;
    private final DemoKnowledgeSaveService demoKnowledgeSaveService;

    @Value("${lumi.ai.demo-owner-login:}")
    private String demoOwnerLogin;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (demoOwnerLogin.isBlank()) {
            throw new IllegalStateException(
                "Cần đặt LUMI_AI_DEMO_OWNER_LOGIN bằng login tài khoản local."
            );
        }

        // 1. Đọc file mẫu, đường dẫn tính từ thư mục chạy backend.
        Path samplePath = Path.of("dev-data/rag/sample-chunks.json");
        String json = Files.readString(samplePath, StandardCharsets.UTF_8);

        var root = objectMapper.readTree(json);
        var chunks = root.path("chunks");

        if (!root.path("demoOnly").asBoolean() || !chunks.isArray() || chunks.isEmpty()) {
            throw new IllegalStateException("File dữ liệu mẫu không hợp lệ.");
        }

        // 2. Chỉ lấy đoạn đầu tiên.
        String text = chunks.get(2).path("chunkText").asText("");

        if (text.isBlank()) {
            throw new IllegalStateException("Đoạn mẫu không có nội dung.");
        }

        // 3. Gọi Gemini thông qua Spring AI.
        float[] vector = embeddingModel.embed(text);

        // 4. Kiểm tra trước khi dùng với cột vector(768).
        if (vector == null || vector.length != 768) {
            throw new IllegalStateException("Embedding phải có đúng 768 chiều.");
        }

        for (float value : vector) {
            if (!Float.isFinite(value)) {
                throw new IllegalStateException("Embedding chứa giá trị không hợp lệ.");
            }
        }

        LOG.info("DEMO EMBEDDING OK: chunkIndex=0, dimensions={}", vector.length);

        Long documentId = demoKnowledgeSaveService.saveFirstChunk(
            demoOwnerLogin,
            text,
            vector
        );

        LOG.info("DEMO SAVE OK: documentId={}", documentId);
    }
}
