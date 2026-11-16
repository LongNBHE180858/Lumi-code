package com.lumi.service.ai.demo;

import com.lumi.domain.LearningDocument;
import com.lumi.domain.PdfChunk;
import com.lumi.repository.LearningDocumentRepository;
import com.lumi.repository.PdfChunkRepository;
import com.lumi.repository.UserRepository;
import java.time.Instant;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(
    name = "lumi.ai.demo-embedding",
    havingValue = "true"
)
public class DemoKnowledgeSaveService {

    // Khóa nhận diện mẫu local, không phải object đã upload lên R2.
    private static final String SAMPLE_KEY =
        "dev-data/rag/sample-chunks.json#first-chunk-v1";

    private final LearningDocumentRepository documentRepository;
    private final PdfChunkRepository chunkRepository;
    private final UserRepository userRepository;

    public DemoKnowledgeSaveService(
        LearningDocumentRepository documentRepository,
        PdfChunkRepository chunkRepository,
        UserRepository userRepository
    ) {
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Long saveFirstChunk(String ownerLogin, String text, float[] vector) {
        // 1. Kiểm tra dữ liệu trước khi ghi.
        if (ownerLogin == null || ownerLogin.isBlank()) {
            throw new IllegalArgumentException("Chưa cấu hình login chủ tài liệu mẫu.");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Nội dung chunk không được rỗng.");
        }
        if (vector == null || vector.length != 768) {
            throw new IllegalArgumentException("Embedding phải có đúng 768 chiều.");
        }
        for (float value : vector) {
            if (!Float.isFinite(value)) {
                throw new IllegalArgumentException("Embedding chứa số không hợp lệ.");
            }
        }

        // 2. Chạy lại thì giữ nguyên mẫu đã lưu.
        var existing = documentRepository
            .findFirstByFileObjectKeyAndSourceTypeAndUploadedBy_Login(
                SAMPLE_KEY, "DEMO", ownerLogin
            );

        if (existing.isPresent()) {
            return existing.get().getId();
        }

        // 3. Người tạo phải là tài khoản đã tồn tại trong DB local.
        var owner = userRepository.findOneByLogin(ownerLogin)
            .orElseThrow(() ->
                new IllegalArgumentException("Không tìm thấy tài khoản: " + ownerLogin)
            );

        Instant now = Instant.now();

        // 4. Lưu tài liệu nguồn trước để lấy ID.
        LearningDocument document = new LearningDocument()
            .title("Dữ liệu thử nghiệm RAG — Toán 6 — đoạn đầu")
            .sourceType("DEMO")
            .fileObjectKey(SAMPLE_KEY)
            .sourceVersion(1)
            .processingStatus("DEMO")
            .indexStatus("DEMO")
            .embeddingModel("gemini-embedding-001")
            .uploadedBy(owner)
            .createdAt(now)
            .updatedAt(now)
            .sourceMetadata(Map.<String, Object>of(
                "demoOnly", true,
                "subjectName", "Toán",
                "gradeLevel", 6,
                "sourceNote", "Nội dung tự soạn để thử kỹ thuật, không phải trích sách."
            ));

        document = documentRepository.save(document);

        // 5. Lưu đoạn văn và vector, liên kết với tài liệu vừa tạo.
        PdfChunk chunk = new PdfChunk()
            .material(document)
            .chunkText(text)
            .embedding(vector)
            .pageNumber(null)
            .chunkIndex(0)
            .indexVersion(1);

        chunkRepository.saveAndFlush(chunk);

        return document.getId();
    }
}
