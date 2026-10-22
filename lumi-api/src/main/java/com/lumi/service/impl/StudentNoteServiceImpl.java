package com.lumi.service.impl;

import com.lumi.domain.StudentNote;
import com.lumi.repository.StudentNoteRepository;
import com.lumi.service.StudentNoteService;
import com.lumi.service.dto.StudentNoteDTO;
import com.lumi.service.mapper.StudentNoteMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.domain.StudentNote}.
 */
@Service
@Transactional
public class StudentNoteServiceImpl implements StudentNoteService {

    private static final Logger LOG = LoggerFactory.getLogger(StudentNoteServiceImpl.class);

    private final StudentNoteRepository studentNoteRepository;

    private final StudentNoteMapper studentNoteMapper;

    public StudentNoteServiceImpl(StudentNoteRepository studentNoteRepository, StudentNoteMapper studentNoteMapper) {
        this.studentNoteRepository = studentNoteRepository;
        this.studentNoteMapper = studentNoteMapper;
    }

    @Override
    public StudentNoteDTO save(StudentNoteDTO studentNoteDTO) {
        LOG.debug("Request to save StudentNote : {}", studentNoteDTO);
        StudentNote studentNote = studentNoteMapper.toEntity(studentNoteDTO);
        studentNote = studentNoteRepository.save(studentNote);
        return studentNoteMapper.toDto(studentNote);
    }

    @Override
    public StudentNoteDTO update(StudentNoteDTO studentNoteDTO) {
        LOG.debug("Request to update StudentNote : {}", studentNoteDTO);
        StudentNote studentNote = studentNoteMapper.toEntity(studentNoteDTO);
        studentNote = studentNoteRepository.save(studentNote);
        return studentNoteMapper.toDto(studentNote);
    }

    @Override
    public Optional<StudentNoteDTO> partialUpdate(StudentNoteDTO studentNoteDTO) {
        LOG.debug("Request to partially update StudentNote : {}", studentNoteDTO);

        return studentNoteRepository
            .findById(studentNoteDTO.getId())
            .map(existingStudentNote -> {
                studentNoteMapper.partialUpdate(existingStudentNote, studentNoteDTO);

                return existingStudentNote;
            })
            .map(studentNoteRepository::save)
            .map(studentNoteMapper::toDto);
    }

    public Page<StudentNoteDTO> findAllWithEagerRelationships(Pageable pageable) {
        return studentNoteRepository.findAllWithEagerRelationships(pageable).map(studentNoteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentNoteDTO> findOne(Long id) {
        LOG.debug("Request to get StudentNote : {}", id);
        return studentNoteRepository.findOneWithEagerRelationships(id).map(studentNoteMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete StudentNote : {}", id);
        studentNoteRepository.deleteById(id);
    }
}
