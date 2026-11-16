package com.lumi.service.ai.ingestion;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

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
// Chỉ chạy khi chủ động bật lumi.ai.preview-chunks=true.
@ConditionalOnProperty(name = "lumi.ai.preview-chunks", havingValue = "true")
public class BookChunksPreviewRunner implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(BookChunksPreviewRunner.class);

    private final ObjectMapper objectMapper;
    private final EmbeddingModel embeddingModel;
    private final BookDocumentSaveService bookDocumentSaveService;
    private final BookChunkSaveService bookChunkSaveService;

    // Tài khoản đứng tên tài liệu; mặc định dùng admin cho lần nạp local.
    @Value("${lumi.ai.ingestion.owner-login:admin}")
    private String ownerLogin;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 1. Đọc file JSON chứa các chunk đã chia sẵn.
        // Đường dẫn tính từ thư mục chạy backend: lumi-api.
        Path path = Path.of("dev-data/rag/sach-mau/bai-01.chunks.json");

        String json = Files.readString(path, StandardCharsets.UTF_8);
        var root = objectMapper.readTree(json);
        var chunks = root.path("chunks");

        if (!chunks.isArray() || chunks.isEmpty()) {
            throw new IllegalStateException("File không có danh sách chunks hợp lệ.");
        }

        // Kiểm tra toàn bộ chunk trước khi bắt đầu ghi DB hoặc gọi Gemini.
        for (int i = 0; i < chunks.size(); i++) {
            var chunk = chunks.get(i);

            if (!chunk.path("chunkIndex").isIntegralNumber() || chunk.path("chunkIndex").asInt(-1) != i || !chunk.path("pageNumber").isIntegralNumber() || chunk.path("pageNumber").asInt(-1) < 1 || !chunk.path("chunkText").isTextual() || chunk.path("chunkText").asText("").isBlank()) {
                throw new IllegalStateException("Chunk không hợp lệ tại vị trí " + i);
            }
        }

        LOG.info("BOOK: title={}, totalChunks={}", root.path("title").asText(), chunks.size());

        // 2. Giữ JSON nguồn trong source_metadata để bảo toàn metadata
        // như môn/lớp, trang in, vị trí trong Markdown và liên kết ảnh.
        @SuppressWarnings("unchecked") Map<String, Object> metadata = objectMapper.convertValue(root, Map.class);

        // Tạo tài liệu nguồn hoặc lấy lại ID nếu đã nạp trước đó.
        Long documentId = bookDocumentSaveService.saveDocument(ownerLogin, root.path("title").asText(""), metadata);

        LOG.info("BOOK DOCUMENT SAVE OK: documentId={}", documentId);

        int savedCount = 0;
        int skippedCount = 0;

        // 3. Xử lý lần lượt từng chunk.
        for (int i = 0; i < chunks.size(); i++) {
            var chunk = chunks.get(i);

            int chunkIndex = chunk.path("chunkIndex").asInt();
            int pageNumber = chunk.path("pageNumber").asInt();
            String text = chunk.path("chunkText").asText();

            // Bỏ qua chunk đã có embedding trong DB.
            // Nếu lần chạy trước dừng giữa chừng, lần này tiếp tục phần còn thiếu.
            if (bookChunkSaveService.alreadySaved(documentId, chunkIndex, pageNumber, text)) {
                skippedCount++;

                LOG.info("BOOK CHUNK SKIPPED: documentId={}, chunkIndex={}", documentId, chunkIndex);

                continue;
            }

            // Preview chỉ dùng cho log. Nội dung đem embedding vẫn là text đầy đủ.
            String preview = text.replaceAll("\\s+", " ").strip();
            preview = preview.substring(0, Math.min(preview.length(), 120));

            LOG.info("CHUNK: index={}, pdfPage={}, characters={}, preview={}", chunkIndex, pageNumber, text.length(), preview);

            // 4. Gọi model embedding để chuyển nội dung thành vector.
            // Lời gọi này nằm ngoài transaction lưu DB.
            float[] vector = embeddingModel.embed(text);

            // Kiểm tra vector trước khi lưu vào cột vector(768).
            if (vector == null || vector.length != 768) {
                throw new IllegalStateException("Embedding không đủ 768 chiều: chunkIndex=" + chunkIndex);
            }

            for (float value : vector) {
                if (!Float.isFinite(value)) {
                    throw new IllegalStateException("Embedding chứa giá trị không hợp lệ: chunkIndex=" + chunkIndex);
                }
            }

            LOG.info("BOOK EMBEDDING OK: chunkIndex={}, dimensions={}", chunkIndex, vector.length);

            // 5. Lưu nội dung, vector, trang và thứ tự vào pdf_chunks.
            // documentId liên kết chunk với tài liệu nguồn qua material_id.
            Long chunkId = bookChunkSaveService.saveChunk(ownerLogin, documentId, chunkIndex, pageNumber, text, vector);

            savedCount++;

            LOG.info("BOOK CHUNK SAVE OK: documentId={}, chunkIndex={}, chunkId={}", documentId, chunkIndex, chunkId);
        }

        LOG.info("BOOK INGESTION OK: documentId={}, saved={}, skipped={}, total={}", documentId, savedCount, skippedCount, chunks.size());
    }
}
