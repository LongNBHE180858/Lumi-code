package com.lumi.service.impl;

import com.lumi.domain.Grade;
import com.lumi.repository.GradeRepository;
import com.lumi.service.GradeService;
import com.lumi.service.dto.GradeDTO;
import com.lumi.service.mapper.GradeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.Grade}.
 */
@Service
@Transactional
public class GradeServiceImpl implements GradeService {

    private static final Logger LOG = LoggerFactory.getLogger(GradeServiceImpl.class);

    private final GradeRepository gradeRepository;

    private final GradeMapper gradeMapper;

    public GradeServiceImpl(GradeRepository gradeRepository, GradeMapper gradeMapper) {
        this.gradeRepository = gradeRepository;
        this.gradeMapper = gradeMapper;
    }

    @Override
    public GradeDTO save(GradeDTO gradeDTO) {
        LOG.debug("Request to save Grade : {}", gradeDTO);
        Grade grade = gradeMapper.toEntity(gradeDTO);
        if (grade.getActive() == null) grade.setActive(true);
        grade = gradeRepository.save(grade);
        return gradeMapper.toDto(grade);
    }

    @Override
    public GradeDTO update(GradeDTO gradeDTO) {
        LOG.debug("Request to update Grade : {}", gradeDTO);
        Grade grade = gradeMapper.toEntity(gradeDTO);
        if (grade.getActive() == null) grade.setActive(gradeRepository.findById(gradeDTO.getId()).map(Grade::getActive).orElse(true));
        grade = gradeRepository.save(grade);
        return gradeMapper.toDto(grade);
    }

    @Override
    public Optional<GradeDTO> partialUpdate(GradeDTO gradeDTO) {
        LOG.debug("Request to partially update Grade : {}", gradeDTO);

        return gradeRepository
            .findById(gradeDTO.getId())
            .map(existingGrade -> {
                gradeMapper.partialUpdate(existingGrade, gradeDTO);

                return existingGrade;
            })
            .map(gradeRepository::save)
            .map(gradeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GradeDTO> findOne(Long id) {
        LOG.debug("Request to get Grade : {}", id);
        return gradeRepository.findById(id).map(gradeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Grade : {}", id);
        gradeRepository.findById(id).ifPresent(grade -> grade.setActive(false));
    }
}
