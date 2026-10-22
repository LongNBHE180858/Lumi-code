package com.lumi.repository;

import com.lumi.domain.PdfChunk;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PdfChunk entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PdfChunkRepository extends JpaRepository<PdfChunk, Long>, JpaSpecificationExecutor<PdfChunk> {}
