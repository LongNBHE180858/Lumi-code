package com.lumi.service.ai.ingestion;

import com.lumi.domain.LearningDocument;
import com.lumi.repository.LearningDocumentRepository;
import com.lumi.repository.UserRepository;

import java.time.Instant;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookDocumentSaveService {

    private static final String SOURCE_KEY = "dev-data/rag/sach-mau/bai-01.chunks.json";

    private final LearningDocumentRepository documentRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long saveDocument(String ownerLogin, String title, Map<String, Object> metadata) {
        if (ownerLogin == null || ownerLogin.isBlank()) {
            throw new IllegalArgumentException("Chưa cấu hình chủ tài liệu.");
        }

        if (title == null || title.isBlank() || title.length() > 255) {
            throw new IllegalArgumentException("Tên tài liệu không hợp lệ.");
        }

        if (metadata == null || metadata.isEmpty()) {
            throw new IllegalArgumentException("Thiếu metadata tài liệu.");
        }

        // Chạy lại cùng file và chủ tài liệu thì dùng tài liệu đã tạo.
        var existing = documentRepository.findFirstByFileObjectKeyAndSourceTypeAndUploadedBy_Login(SOURCE_KEY, "DEMO", ownerLogin);

        if (existing.isPresent()) {
            return existing.get().getId();
        }

        var owner = userRepository.findOneByLogin(ownerLogin).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản: " + ownerLogin));

        Instant now = Instant.now();

        LearningDocument document = new LearningDocument().title(title).sourceType("DEMO").fileObjectKey(SOURCE_KEY).sourceVersion(1).processingStatus("DEMO").indexStatus("DEMO").embeddingModel("gemini-embedding-001").sourceMetadata(metadata).uploadedBy(owner).createdAt(now).updatedAt(now);

        return documentRepository.saveAndFlush(document).getId();
    }
}
