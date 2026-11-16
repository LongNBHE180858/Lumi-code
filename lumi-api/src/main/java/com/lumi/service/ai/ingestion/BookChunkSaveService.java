package com.lumi.service.ai.ingestion;

import com.lumi.domain.PdfChunk;
import com.lumi.repository.LearningDocumentRepository;
import com.lumi.repository.PdfChunkRepository;

import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookChunkSaveService {

    private final LearningDocumentRepository documentRepository;
    private final PdfChunkRepository chunkRepository;

    @Transactional(readOnly = true)
    public boolean alreadySaved(Long documentId, int chunkIndex, int pageNumber, String text) {
        var existing = chunkRepository.findFirstByMaterial_IdAndChunkIndexAndIndexVersion(documentId, chunkIndex, 1);

        if (existing.isEmpty()) {
            return false;
        }

        var saved = existing.get();

        if (!Objects.equals(saved.getChunkText(), text) || !Objects.equals(saved.getPageNumber(), pageNumber)) {
            throw new IllegalStateException("Chunk đã lưu khác dữ liệu hiện tại: index=" + chunkIndex);
        }

        return saved.getEmbedding() != null;
    }

    @Transactional
    public Long saveChunk(String ownerLogin, Long documentId, int chunkIndex, int pageNumber, String text, float[] vector) {
        if (chunkIndex < 0 || pageNumber < 1 || text == null || text.isBlank()) {
            throw new IllegalArgumentException("Dữ liệu chunk không hợp lệ.");
        }

        if (vector == null || vector.length != 768) {
            throw new IllegalArgumentException("Vector phải có 768 chiều.");
        }

        for (float value : vector) {
            if (!Float.isFinite(value)) {
                throw new IllegalArgumentException("Vector chứa số không hợp lệ.");
            }
        }

        var document = documentRepository.findById(documentId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài liệu."));

        if (!Objects.equals(document.getUploadedBy().getLogin(), ownerLogin)) {
            throw new IllegalArgumentException("Tài liệu không thuộc tài khoản nạp dữ liệu.");
        }

        var existing = chunkRepository.findFirstByMaterial_IdAndChunkIndexAndIndexVersion(documentId, chunkIndex, 1);

        if (existing.isPresent()) {
            var saved = existing.get();

            if (!Objects.equals(saved.getChunkText(), text) || !Objects.equals(saved.getPageNumber(), pageNumber)) {
                throw new IllegalStateException("Chunk đã lưu khác dữ liệu hiện tại: index=" + chunkIndex);
            }

            // Bổ sung embedding nếu bản ghi cũ chưa có.
            if (saved.getEmbedding() == null) {
                saved.setEmbedding(vector);
                chunkRepository.saveAndFlush(saved);
            }

            return saved.getId();
        }

        PdfChunk chunk = new PdfChunk().material(document).chunkIndex(chunkIndex).pageNumber(pageNumber).chunkText(text).embedding(vector).indexVersion(1);

        return chunkRepository.saveAndFlush(chunk).getId();
    }
}
