package com.lumi.service.impl;

import com.lumi.domain.ClassMaterial;
import com.lumi.repository.ClassMaterialRepository;
import com.lumi.service.ClassMaterialService;
import com.lumi.service.dto.ClassMaterialDTO;
import com.lumi.service.mapper.ClassMaterialMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.ClassMaterial}.
 */
@Service
@Transactional
public class ClassMaterialServiceImpl implements ClassMaterialService {

    private static final Logger LOG = LoggerFactory.getLogger(ClassMaterialServiceImpl.class);

    private final ClassMaterialRepository classMaterialRepository;

    private final ClassMaterialMapper classMaterialMapper;

    public ClassMaterialServiceImpl(ClassMaterialRepository classMaterialRepository, ClassMaterialMapper classMaterialMapper) {
        this.classMaterialRepository = classMaterialRepository;
        this.classMaterialMapper = classMaterialMapper;
    }

    @Override
    public ClassMaterialDTO save(ClassMaterialDTO classMaterialDTO) {
        LOG.debug("Request to save ClassMaterial : {}", classMaterialDTO);
        ClassMaterial classMaterial = classMaterialMapper.toEntity(classMaterialDTO);
        classMaterial = classMaterialRepository.save(classMaterial);
        return classMaterialMapper.toDto(classMaterial);
    }

    @Override
    public ClassMaterialDTO update(ClassMaterialDTO classMaterialDTO) {
        LOG.debug("Request to update ClassMaterial : {}", classMaterialDTO);
        ClassMaterial classMaterial = classMaterialMapper.toEntity(classMaterialDTO);
        classMaterial = classMaterialRepository.save(classMaterial);
        return classMaterialMapper.toDto(classMaterial);
    }

    @Override
    public Optional<ClassMaterialDTO> partialUpdate(ClassMaterialDTO classMaterialDTO) {
        LOG.debug("Request to partially update ClassMaterial : {}", classMaterialDTO);

        return classMaterialRepository
            .findById(classMaterialDTO.getId())
            .map(existingClassMaterial -> {
                classMaterialMapper.partialUpdate(existingClassMaterial, classMaterialDTO);

                return existingClassMaterial;
            })
            .map(classMaterialRepository::save)
            .map(classMaterialMapper::toDto);
    }

    public Page<ClassMaterialDTO> findAllWithEagerRelationships(Pageable pageable) {
        return classMaterialRepository.findAllWithEagerRelationships(pageable).map(classMaterialMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClassMaterialDTO> findOne(Long id) {
        LOG.debug("Request to get ClassMaterial : {}", id);
        return classMaterialRepository.findOneWithEagerRelationships(id).map(classMaterialMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ClassMaterial : {}", id);
        classMaterialRepository.deleteById(id);
    }
}
