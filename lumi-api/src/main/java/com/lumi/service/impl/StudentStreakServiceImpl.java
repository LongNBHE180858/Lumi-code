package com.lumi.service.impl;

import com.lumi.domain.StudentStreak;
import com.lumi.repository.StudentStreakRepository;
import com.lumi.service.StudentStreakService;
import com.lumi.service.dto.StudentStreakDTO;
import com.lumi.service.mapper.StudentStreakMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.StudentStreak}.
 */
@Service
@Transactional
public class StudentStreakServiceImpl implements StudentStreakService {

    private static final Logger LOG = LoggerFactory.getLogger(StudentStreakServiceImpl.class);

    private final StudentStreakRepository studentStreakRepository;

    private final StudentStreakMapper studentStreakMapper;

    public StudentStreakServiceImpl(StudentStreakRepository studentStreakRepository, StudentStreakMapper studentStreakMapper) {
        this.studentStreakRepository = studentStreakRepository;
        this.studentStreakMapper = studentStreakMapper;
    }

    @Override
    public StudentStreakDTO save(StudentStreakDTO studentStreakDTO) {
        LOG.debug("Request to save StudentStreak : {}", studentStreakDTO);
        StudentStreak studentStreak = studentStreakMapper.toEntity(studentStreakDTO);
        studentStreak = studentStreakRepository.save(studentStreak);
        return studentStreakMapper.toDto(studentStreak);
    }

    @Override
    public StudentStreakDTO update(StudentStreakDTO studentStreakDTO) {
        LOG.debug("Request to update StudentStreak : {}", studentStreakDTO);
        StudentStreak studentStreak = studentStreakMapper.toEntity(studentStreakDTO);
        studentStreak = studentStreakRepository.save(studentStreak);
        return studentStreakMapper.toDto(studentStreak);
    }

    @Override
    public Optional<StudentStreakDTO> partialUpdate(StudentStreakDTO studentStreakDTO) {
        LOG.debug("Request to partially update StudentStreak : {}", studentStreakDTO);

        return studentStreakRepository
            .findById(studentStreakDTO.getId())
            .map(existingStudentStreak -> {
                studentStreakMapper.partialUpdate(existingStudentStreak, studentStreakDTO);

                return existingStudentStreak;
            })
            .map(studentStreakRepository::save)
            .map(studentStreakMapper::toDto);
    }

    public List<StudentStreakDTO> findAllWithEagerRelationships() {
        return studentStreakRepository
            .findAllWithEagerRelationships()
            .stream()
            .map(studentStreakMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentStreakDTO> findOne(Long id) {
        LOG.debug("Request to get StudentStreak : {}", id);
        return studentStreakRepository.findOneWithEagerRelationships(id).map(studentStreakMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete StudentStreak : {}", id);
        studentStreakRepository.deleteById(id);
    }
}
