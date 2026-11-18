package com.lumi.repository;

import com.lumi.domain.PdfChunk;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the PdfChunk entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PdfChunkRepository extends JpaRepository<PdfChunk, Long>, JpaSpecificationExecutor<PdfChunk> {
    interface RetrievedChunk {
        Long getChunkId();
        Long getDocumentId();
        String getDocumentTitle();
        Integer getPageNumber();
        String getChunkText();
        Double getDistance();
    }
    // <=> :tính khoảng cách cosine giữa hai vector.
    // Kho nguồn AI dùng chung; uploaded_by chỉ ghi nhận người nạp tài liệu.
    @Query(
        value = """
        SELECT
            c.id AS "chunkId",
            d.id AS "documentId",
            d.title AS "documentTitle",
            c.page_number AS "pageNumber",
            c.chunk_text AS "chunkText",
            c.embedding <=> CAST(:queryVector AS vector) AS "distance"
        FROM pdf_chunks c
        JOIN learning_documents d ON d.id = c.material_id
        WHERE d.source_type = 'DEMO'
          AND d.source_metadata ->> 'demoOnly' = 'true'
          AND d.embedding_model = 'gemini-embedding-001'
          AND c.index_version = 1
          AND c.embedding IS NOT NULL
        ORDER BY c.embedding <=> CAST(:queryVector AS vector) ASC
        LIMIT 3
        """,
        nativeQuery = true
    )
    List<RetrievedChunk> findNearestChunks(
        @Param("queryVector") String queryVector
    );

    Optional<PdfChunk> findFirstByMaterial_IdAndChunkIndexAndIndexVersion(
        Long documentId,
        Integer chunkIndex,
        Integer indexVersion
    );
}
