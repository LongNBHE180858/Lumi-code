package com.lumi.service.impl;

import com.lumi.domain.Lesson;
import com.lumi.repository.LessonRepository;
import com.lumi.service.LessonService;
import com.lumi.service.dto.LessonDTO;
import com.lumi.service.mapper.LessonMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.Lesson}.
 */
@Service
@Transactional
public class LessonServiceImpl implements LessonService {

    private static final Logger LOG = LoggerFactory.getLogger(LessonServiceImpl.class);

    private final LessonRepository lessonRepository;

    private final LessonMapper lessonMapper;

    public LessonServiceImpl(LessonRepository lessonRepository, LessonMapper lessonMapper) {
        this.lessonRepository = lessonRepository;
        this.lessonMapper = lessonMapper;
    }

    @Override
    public LessonDTO save(LessonDTO lessonDTO) {
        LOG.debug("Request to save Lesson : {}", lessonDTO);
        Lesson lesson = lessonMapper.toEntity(lessonDTO);
        lesson = lessonRepository.save(lesson);
        return lessonMapper.toDto(lesson);
    }

    @Override
    public LessonDTO update(LessonDTO lessonDTO) {
        LOG.debug("Request to update Lesson : {}", lessonDTO);
        Lesson lesson = lessonMapper.toEntity(lessonDTO);
        lesson = lessonRepository.save(lesson);
        return lessonMapper.toDto(lesson);
    }

    @Override
    public Optional<LessonDTO> partialUpdate(LessonDTO lessonDTO) {
        LOG.debug("Request to partially update Lesson : {}", lessonDTO);

        return lessonRepository
            .findById(lessonDTO.getId())
            .map(existingLesson -> {
                lessonMapper.partialUpdate(existingLesson, lessonDTO);

                return existingLesson;
            })
            .map(lessonRepository::save)
            .map(lessonMapper::toDto);
    }

    public Page<LessonDTO> findAllWithEagerRelationships(Pageable pageable) {
        return lessonRepository.findAllWithEagerRelationships(pageable).map(lessonMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LessonDTO> findOne(Long id) {
        LOG.debug("Request to get Lesson : {}", id);
        return lessonRepository.findOneWithEagerRelationships(id).map(lessonMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Lesson : {}", id);
        lessonRepository.deleteById(id);
    }
}
