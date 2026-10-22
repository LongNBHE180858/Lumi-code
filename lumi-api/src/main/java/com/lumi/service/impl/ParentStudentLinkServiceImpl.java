package com.lumi.service.impl;

import com.lumi.domain.ParentStudentLink;
import com.lumi.repository.ParentStudentLinkRepository;
import com.lumi.service.ParentStudentLinkService;
import com.lumi.service.dto.ParentStudentLinkDTO;
import com.lumi.service.mapper.ParentStudentLinkMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.ParentStudentLink}.
 */
@Service
@Transactional
public class ParentStudentLinkServiceImpl implements ParentStudentLinkService {

    private static final Logger LOG = LoggerFactory.getLogger(ParentStudentLinkServiceImpl.class);

    private final ParentStudentLinkRepository parentStudentLinkRepository;

    private final ParentStudentLinkMapper parentStudentLinkMapper;

    public ParentStudentLinkServiceImpl(
        ParentStudentLinkRepository parentStudentLinkRepository,
        ParentStudentLinkMapper parentStudentLinkMapper
    ) {
        this.parentStudentLinkRepository = parentStudentLinkRepository;
        this.parentStudentLinkMapper = parentStudentLinkMapper;
    }

    @Override
    public ParentStudentLinkDTO save(ParentStudentLinkDTO parentStudentLinkDTO) {
        LOG.debug("Request to save ParentStudentLink : {}", parentStudentLinkDTO);
        ParentStudentLink parentStudentLink = parentStudentLinkMapper.toEntity(parentStudentLinkDTO);
        parentStudentLink = parentStudentLinkRepository.save(parentStudentLink);
        return parentStudentLinkMapper.toDto(parentStudentLink);
    }

    @Override
    public ParentStudentLinkDTO update(ParentStudentLinkDTO parentStudentLinkDTO) {
        LOG.debug("Request to update ParentStudentLink : {}", parentStudentLinkDTO);
        ParentStudentLink parentStudentLink = parentStudentLinkMapper.toEntity(parentStudentLinkDTO);
        parentStudentLink = parentStudentLinkRepository.save(parentStudentLink);
        return parentStudentLinkMapper.toDto(parentStudentLink);
    }

    @Override
    public Optional<ParentStudentLinkDTO> partialUpdate(ParentStudentLinkDTO parentStudentLinkDTO) {
        LOG.debug("Request to partially update ParentStudentLink : {}", parentStudentLinkDTO);

        return parentStudentLinkRepository
            .findById(parentStudentLinkDTO.getId())
            .map(existingParentStudentLink -> {
                parentStudentLinkMapper.partialUpdate(existingParentStudentLink, parentStudentLinkDTO);

                return existingParentStudentLink;
            })
            .map(parentStudentLinkRepository::save)
            .map(parentStudentLinkMapper::toDto);
    }

    public List<ParentStudentLinkDTO> findAllWithEagerRelationships() {
        return parentStudentLinkRepository
            .findAllWithEagerRelationships()
            .stream()
            .map(parentStudentLinkMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ParentStudentLinkDTO> findOne(Long id) {
        LOG.debug("Request to get ParentStudentLink : {}", id);
        return parentStudentLinkRepository.findOneWithEagerRelationships(id).map(parentStudentLinkMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ParentStudentLink : {}", id);
        parentStudentLinkRepository.deleteById(id);
    }
}
