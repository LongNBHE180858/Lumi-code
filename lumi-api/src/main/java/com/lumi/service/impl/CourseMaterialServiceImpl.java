package com.lumi.service.impl;

import com.lumi.domain.CourseMaterial;
import com.lumi.repository.CourseMaterialRepository;
import com.lumi.service.CourseMaterialService;
import com.lumi.service.dto.CourseMaterialDTO;
import com.lumi.service.mapper.CourseMaterialMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.CourseMaterial}.
 */
@Service
@Transactional
public class CourseMaterialServiceImpl implements CourseMaterialService {

    private static final Logger LOG = LoggerFactory.getLogger(CourseMaterialServiceImpl.class);

    private final CourseMaterialRepository courseMaterialRepository;

    private final CourseMaterialMapper courseMaterialMapper;

    public CourseMaterialServiceImpl(CourseMaterialRepository courseMaterialRepository, CourseMaterialMapper courseMaterialMapper) {
        this.courseMaterialRepository = courseMaterialRepository;
        this.courseMaterialMapper = courseMaterialMapper;
    }

    @Override
    public CourseMaterialDTO save(CourseMaterialDTO courseMaterialDTO) {
        LOG.debug("Request to save CourseMaterial : {}", courseMaterialDTO);
        CourseMaterial courseMaterial = courseMaterialMapper.toEntity(courseMaterialDTO);
        courseMaterial = courseMaterialRepository.save(courseMaterial);
        return courseMaterialMapper.toDto(courseMaterial);
    }

    @Override
    public CourseMaterialDTO update(CourseMaterialDTO courseMaterialDTO) {
        LOG.debug("Request to update CourseMaterial : {}", courseMaterialDTO);
        CourseMaterial courseMaterial = courseMaterialMapper.toEntity(courseMaterialDTO);
        courseMaterial = courseMaterialRepository.save(courseMaterial);
        return courseMaterialMapper.toDto(courseMaterial);
    }

    @Override
    public Optional<CourseMaterialDTO> partialUpdate(CourseMaterialDTO courseMaterialDTO) {
        LOG.debug("Request to partially update CourseMaterial : {}", courseMaterialDTO);

        return courseMaterialRepository
            .findById(courseMaterialDTO.getId())
            .map(existingCourseMaterial -> {
                courseMaterialMapper.partialUpdate(existingCourseMaterial, courseMaterialDTO);

                return existingCourseMaterial;
            })
            .map(courseMaterialRepository::save)
            .map(courseMaterialMapper::toDto);
    }

    public Page<CourseMaterialDTO> findAllWithEagerRelationships(Pageable pageable) {
        return courseMaterialRepository.findAllWithEagerRelationships(pageable).map(courseMaterialMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseMaterialDTO> findOne(Long id) {
        LOG.debug("Request to get CourseMaterial : {}", id);
        return courseMaterialRepository.findOneWithEagerRelationships(id).map(courseMaterialMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CourseMaterial : {}", id);
        courseMaterialRepository.deleteById(id);
    }
}
