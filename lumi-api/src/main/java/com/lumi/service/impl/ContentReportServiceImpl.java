package com.lumi.service.impl;

import com.lumi.domain.ContentReport;
import com.lumi.repository.ContentReportRepository;
import com.lumi.service.ContentReportService;
import com.lumi.service.dto.ContentReportDTO;
import com.lumi.service.mapper.ContentReportMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.ContentReport}.
 */
@Service
@Transactional
public class ContentReportServiceImpl implements ContentReportService {

    private static final Logger LOG = LoggerFactory.getLogger(ContentReportServiceImpl.class);

    private final ContentReportRepository contentReportRepository;

    private final ContentReportMapper contentReportMapper;

    public ContentReportServiceImpl(ContentReportRepository contentReportRepository, ContentReportMapper contentReportMapper) {
        this.contentReportRepository = contentReportRepository;
        this.contentReportMapper = contentReportMapper;
    }

    @Override
    public ContentReportDTO save(ContentReportDTO contentReportDTO) {
        LOG.debug("Request to save ContentReport : {}", contentReportDTO);
        ContentReport contentReport = contentReportMapper.toEntity(contentReportDTO);
        contentReport = contentReportRepository.save(contentReport);
        return contentReportMapper.toDto(contentReport);
    }

    @Override
    public ContentReportDTO update(ContentReportDTO contentReportDTO) {
        LOG.debug("Request to update ContentReport : {}", contentReportDTO);
        ContentReport contentReport = contentReportMapper.toEntity(contentReportDTO);
        contentReport = contentReportRepository.save(contentReport);
        return contentReportMapper.toDto(contentReport);
    }

    @Override
    public Optional<ContentReportDTO> partialUpdate(ContentReportDTO contentReportDTO) {
        LOG.debug("Request to partially update ContentReport : {}", contentReportDTO);

        return contentReportRepository
            .findById(contentReportDTO.getId())
            .map(existingContentReport -> {
                contentReportMapper.partialUpdate(existingContentReport, contentReportDTO);

                return existingContentReport;
            })
            .map(contentReportRepository::save)
            .map(contentReportMapper::toDto);
    }

    public Page<ContentReportDTO> findAllWithEagerRelationships(Pageable pageable) {
        return contentReportRepository.findAllWithEagerRelationships(pageable).map(contentReportMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ContentReportDTO> findOne(Long id) {
        LOG.debug("Request to get ContentReport : {}", id);
        return contentReportRepository.findOneWithEagerRelationships(id).map(contentReportMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ContentReport : {}", id);
        contentReportRepository.deleteById(id);
    }
}
