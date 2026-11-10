package com.lumi.service.impl;

import com.lumi.domain.StudyClass;
import com.lumi.domain.enumeration.ClassJoinMode;
import com.lumi.repository.StudyClassRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.SecurityUtils;
import com.lumi.service.InstructorAccessService;
import com.lumi.service.StudyClassService;
import com.lumi.service.dto.StudyClassDTO;
import com.lumi.service.dto.studyClass.CreateStudyClassRequestDTO;
import com.lumi.service.dto.studyClass.StudyClassDetailDTO;
import com.lumi.service.dto.studyClass.UpdateStudyClassRequestDTO;
import com.lumi.service.mapper.StudyClassMapper;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service Implementation for managing {@link com.lumi.domain.StudyClass}.
 */
@Service
@Transactional
public class StudyClassServiceImpl implements StudyClassService {

    private static final Logger LOG = LoggerFactory.getLogger(StudyClassServiceImpl.class);

    private final StudyClassRepository studyClassRepository;
    private final UserRepository userRepository;
    private final InstructorAccessService instructorAccess;

    private final StudyClassMapper studyClassMapper;
    private final SecureRandom secureRandom = new SecureRandom();

    public StudyClassServiceImpl(
        StudyClassRepository studyClassRepository,
        StudyClassMapper studyClassMapper,
        UserRepository userRepository,
        InstructorAccessService instructorAccess
    ) {
        this.studyClassRepository = studyClassRepository;
        this.studyClassMapper = studyClassMapper;
        this.userRepository = userRepository;
        this.instructorAccess = instructorAccess;
    }

    @Override
    public List<StudyClassDetailDTO> listClass() {
        instructorAccess.requireActiveInstructor();
        Long currentUserId = SecurityUtils.getCurrentUserId().orElseThrow(() ->
            new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vui lòng đăng nhập")
        );

        var instructor = userRepository
            .findById(currentUserId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản"));

        List<StudyClass> listStudyClass = studyClassRepository.findAllByInstructor_IdOrderByCreatedAtAsc(currentUserId);
        return studyClassMapper.toStudyClassDetailList(listStudyClass);
    }

    @Override
    public StudyClassDetailDTO createClass(CreateStudyClassRequestDTO request) {
        instructorAccess.requireActiveInstructor();
        Long currentUserId = SecurityUtils.getCurrentUserId().orElseThrow(() ->
            new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vui lòng đăng nhập")
        );

        var instructor = userRepository
            .findById(currentUserId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản"));

        StudyClass newStudyClass = studyClassMapper.toStudyClass(request);
        newStudyClass.joinCode(generateCode(6));
        newStudyClass.joinMode(ClassJoinMode.CODE_AUTO);
        newStudyClass.maxStudents(request.getMaxStudents());
        newStudyClass.currentStudents(0);
        newStudyClass.instructor(instructor);

        studyClassRepository.save(newStudyClass);
        return studyClassMapper.toStudyClassDetail(newStudyClass);
    }

    @Override
    public StudyClassDetailDTO updateClass(long id, UpdateStudyClassRequestDTO request) {
        instructorAccess.requireActiveInstructor();
        Long currentUserId = SecurityUtils.getCurrentUserId().orElseThrow(() ->
            new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vui lòng đăng nhập")
        );

        var instructor = userRepository
            .findById(currentUserId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản"));

        StudyClass studyClass = studyClassRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp học"));

        studyClassMapper.updateStudyClass(studyClass, request);

        studyClassRepository.save(studyClass);
        return studyClassMapper.toStudyClassDetail(studyClass);
    }

    private String generateCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    public StudyClassDTO save(StudyClassDTO studyClassDTO) {
        LOG.debug("Request to save StudyClass : {}", studyClassDTO);
        StudyClass studyClass = studyClassMapper.toEntity(studyClassDTO);
        studyClass = studyClassRepository.save(studyClass);
        return studyClassMapper.toDto(studyClass);
    }

    @Override
    public StudyClassDTO update(StudyClassDTO studyClassDTO) {
        LOG.debug("Request to update StudyClass : {}", studyClassDTO);
        StudyClass studyClass = studyClassMapper.toEntity(studyClassDTO);
        studyClass = studyClassRepository.save(studyClass);
        return studyClassMapper.toDto(studyClass);
    }

    @Override
    public Optional<StudyClassDTO> partialUpdate(StudyClassDTO studyClassDTO) {
        LOG.debug("Request to partially update StudyClass : {}", studyClassDTO);

        return studyClassRepository
            .findById(studyClassDTO.getId())
            .map(existingStudyClass -> {
                studyClassMapper.partialUpdate(existingStudyClass, studyClassDTO);

                return existingStudyClass;
            })
            .map(studyClassRepository::save)
            .map(studyClassMapper::toDto);
    }

    public Page<StudyClassDTO> findAllWithEagerRelationships(Pageable pageable) {
        return studyClassRepository.findAllWithEagerRelationships(pageable).map(studyClassMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudyClassDTO> findOne(Long id) {
        LOG.debug("Request to get StudyClass : {}", id);
        return studyClassRepository.findOneWithEagerRelationships(id).map(studyClassMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete StudyClass : {}", id);
        studyClassRepository.deleteById(id);
    }
}
