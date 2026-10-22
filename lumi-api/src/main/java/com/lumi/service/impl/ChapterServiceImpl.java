package com.lumi.service.impl;

import com.lumi.domain.Chapter;
import com.lumi.repository.ChapterRepository;
import com.lumi.service.ChapterService;
import com.lumi.service.dto.ChapterDTO;
import com.lumi.service.mapper.ChapterMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.Chapter}.
 */
@Service
@Transactional
public class ChapterServiceImpl implements ChapterService {

    private final jakarta.persistence.EntityManager entityManager;

    private static final Logger LOG = LoggerFactory.getLogger(ChapterServiceImpl.class);

    private final ChapterRepository chapterRepository;

    private final ChapterMapper chapterMapper;

    public ChapterServiceImpl(
        ChapterRepository chapterRepository,
        ChapterMapper chapterMapper,
        jakarta.persistence.EntityManager entityManager
    ) {
        this.entityManager = entityManager;
        this.chapterRepository = chapterRepository;
        this.chapterMapper = chapterMapper;
    }

    @Override
    public ChapterDTO save(ChapterDTO chapterDTO) {
        LOG.debug("Request to save Chapter : {}", chapterDTO);
        Chapter chapter = chapterMapper.toEntity(chapterDTO);
        if (chapterDTO.getCourse() != null && chapterDTO.getCourse().getId() != null) {
            chapter.setCourse(entityManager.getReference(com.lumi.domain.Course.class, chapterDTO.getCourse().getId()));
        }
        chapter = chapterRepository.save(chapter);
        return chapterMapper.toDto(chapter);
    }

    @Override
    public ChapterDTO update(ChapterDTO chapterDTO) {
        LOG.debug("Request to update Chapter : {}", chapterDTO);
        Chapter chapter = chapterMapper.toEntity(chapterDTO);
        if (chapterDTO.getCourse() != null && chapterDTO.getCourse().getId() != null) {
            chapter.setCourse(entityManager.getReference(com.lumi.domain.Course.class, chapterDTO.getCourse().getId()));
        }
        chapter = chapterRepository.save(chapter);
        return chapterMapper.toDto(chapter);
    }

    @Override
    public Optional<ChapterDTO> partialUpdate(ChapterDTO chapterDTO) {
        LOG.debug("Request to partially update Chapter : {}", chapterDTO);

        return chapterRepository
            .findById(chapterDTO.getId())
            .map(existingChapter -> {
                chapterMapper.partialUpdate(existingChapter, chapterDTO);
                if (chapterDTO.getCourse() != null && chapterDTO.getCourse().getId() != null) {
                    existingChapter.setCourse(entityManager.getReference(com.lumi.domain.Course.class, chapterDTO.getCourse().getId()));
                }

                return existingChapter;
            })
            .map(chapterRepository::save)
            .map(chapterMapper::toDto);
    }

    public Page<ChapterDTO> findAllWithEagerRelationships(Pageable pageable) {
        return chapterRepository.findAllWithEagerRelationships(pageable).map(chapterMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChapterDTO> findOne(Long id) {
        LOG.debug("Request to get Chapter : {}", id);
        return chapterRepository.findOneWithEagerRelationships(id).map(chapterMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Chapter : {}", id);
        chapterRepository.deleteById(id);
    }
}
