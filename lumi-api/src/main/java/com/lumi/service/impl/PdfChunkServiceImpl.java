package com.lumi.service.impl;

import com.lumi.domain.PdfChunk;
import com.lumi.repository.PdfChunkRepository;
import com.lumi.service.PdfChunkService;
import com.lumi.service.dto.PdfChunkDTO;
import com.lumi.service.mapper.PdfChunkMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.PdfChunk}.
 */
@Service
@Transactional
public class PdfChunkServiceImpl implements PdfChunkService {

    private static final Logger LOG = LoggerFactory.getLogger(PdfChunkServiceImpl.class);

    private final PdfChunkRepository pdfChunkRepository;

    private final PdfChunkMapper pdfChunkMapper;

    public PdfChunkServiceImpl(PdfChunkRepository pdfChunkRepository, PdfChunkMapper pdfChunkMapper) {
        this.pdfChunkRepository = pdfChunkRepository;
        this.pdfChunkMapper = pdfChunkMapper;
    }

    @Override
    public PdfChunkDTO save(PdfChunkDTO pdfChunkDTO) {
        LOG.debug("Request to save PdfChunk : {}", pdfChunkDTO);
        PdfChunk pdfChunk = pdfChunkMapper.toEntity(pdfChunkDTO);
        pdfChunk = pdfChunkRepository.save(pdfChunk);
        return pdfChunkMapper.toDto(pdfChunk);
    }

    @Override
    public PdfChunkDTO update(PdfChunkDTO pdfChunkDTO) {
        LOG.debug("Request to update PdfChunk : {}", pdfChunkDTO);
        PdfChunk pdfChunk = pdfChunkMapper.toEntity(pdfChunkDTO);
        pdfChunk = pdfChunkRepository.save(pdfChunk);
        return pdfChunkMapper.toDto(pdfChunk);
    }

    @Override
    public Optional<PdfChunkDTO> partialUpdate(PdfChunkDTO pdfChunkDTO) {
        LOG.debug("Request to partially update PdfChunk : {}", pdfChunkDTO);

        return pdfChunkRepository
            .findById(pdfChunkDTO.getId())
            .map(existingPdfChunk -> {
                pdfChunkMapper.partialUpdate(existingPdfChunk, pdfChunkDTO);

                return existingPdfChunk;
            })
            .map(pdfChunkRepository::save)
            .map(pdfChunkMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PdfChunkDTO> findOne(Long id) {
        LOG.debug("Request to get PdfChunk : {}", id);
        return pdfChunkRepository.findById(id).map(pdfChunkMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete PdfChunk : {}", id);
        pdfChunkRepository.deleteById(id);
    }
}
