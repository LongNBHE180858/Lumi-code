package com.lumi.service.impl;

import com.lumi.domain.StudyClass;
import com.lumi.domain.User;
import com.lumi.domain.enumeration.ClassMemberStatus;
import com.lumi.domain.enumeration.RecordStatus;
import com.lumi.repository.ClassMemberRepository;
import com.lumi.repository.StudyClassRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.security.SecurityUtils;
import com.lumi.service.InstructorAccessService;
import com.lumi.service.StudyClassService;
import com.lumi.service.dto.PageResponseDTO;
import com.lumi.service.dto.studyClass.*;
import com.lumi.service.dto.studyClass.CreateStudyClassRequestDTO;
import com.lumi.service.dto.studyClass.StudyClassDetailDTO;
import com.lumi.service.dto.studyClass.UpdateStudyClassRequestDTO;
import com.lumi.service.mapper.StudyClassMapper;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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
    private final StudyClassMapper studyClassMapper;
    private final SecureRandom secureRandom = new SecureRandom();
    private final ClassMemberRepository classMemberRepository;

    public StudyClassServiceImpl(
        StudyClassRepository studyClassRepository,
        StudyClassMapper studyClassMapper,
        UserRepository userRepository,
        ClassMemberRepository classMemberRepository
    ) {
        this.studyClassRepository = studyClassRepository;
        this.studyClassMapper = studyClassMapper;
        this.userRepository = userRepository;
        this.classMemberRepository = classMemberRepository;
    }

    public Long requireInstructorId() {
        String currentLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản không có quyền truy cập")
        );

        User user = userRepository
            .findOneByLogin(currentLogin)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản không có quyền truy cập"));

        if (!user.isActivated()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản đã bị inactive");
        }

        boolean isInstructor = user
            .getAuthorities()
            .stream()
            .anyMatch(authority -> AuthoritiesConstants.INSTRUCTOR.equals(authority.getName()));

        if (!isInstructor) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Yêu cầu có role giáo viên");
        }

        return user.getId();
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public PageResponseDTO<InstructorClassListItemDTO> listClass(int page, int size, String keyword, RecordStatus status) {
        Long currentUserId = requireInstructorId();

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));

        Specification<StudyClass> filter = (root, query, cb) ->
            cb.and(
                cb.equal(root.get("instructor").get("id"), currentUserId),
                cb.or(cb.isFalse(root.get("isDeleted")), cb.isNull(root.get("isDeleted")))
            );
        if (status != null) {
            filter = filter.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (keyword != null && !keyword.isBlank()) {
            String escapedKeyword = keyword.trim().toLowerCase(Locale.ROOT).replace("!", "!!").replace("%", "!%").replace("_", "!_");
            filter = filter.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + escapedKeyword + "%", '!'));
        }

        Page<StudyClass> classPage = studyClassRepository.findAll(filter, pageRequest);

        if (classPage.isEmpty()) {
            return PageResponseDTO.from(classPage.map(studyClass -> studyClassMapper.toInstructorClassList(studyClass, 0L)));
        }

        List<Long> classIds = classPage.getContent().stream().map(StudyClass::getId).toList();

        List<ClassMemberCountDTO> counts = classMemberRepository.countMembersByClassIds(
            currentUserId,
            classIds,
            List.of(ClassMemberStatus.ACTIVE.name())
        );
        Map<Long, Long> countMap = counts
            .stream()
            .collect(Collectors.toMap(ClassMemberCountDTO::classId, ClassMemberCountDTO::memberCount));

        return PageResponseDTO.from(
            classPage.map(studyClass -> {
                long studentCount = countMap.getOrDefault(studyClass.getId(), 0L);
                return studyClassMapper.toInstructorClassList(studyClass, studentCount);
            })
        );
    }

    @Override
    public void deleteClass(long id) {
        Long instructorId = requireInstructorId();
        StudyClass studyClass = studyClassRepository
            .findByIdForUpdate(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp học"));
        if (Boolean.TRUE.equals(studyClass.getIsDeleted()) || !Objects.equals(studyClass.getInstructor().getId(), instructorId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp học");
        }
        studyClass.setIsDeleted(true);
        studyClass.setUpdatedAt(Instant.now());
        studyClassRepository.save(studyClass);
    }

    @Override
    public StudyClassDetailDTO createClass(CreateStudyClassRequestDTO request) {
        Long currentUserId = requireInstructorId();
        var instructor = userRepository
            .findById(currentUserId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản"));

        StudyClass newStudyClass = studyClassMapper.toStudyClass(request);
        newStudyClass.joinCode(generateUniqueCode());
        newStudyClass.joinMode(request.getJoinMode());
        newStudyClass.maxStudents(request.getMaxStudents());
        newStudyClass.currentStudents(0);
        newStudyClass.instructor(instructor);
        newStudyClass.status(request.getStatus());
        newStudyClass.isDeleted(false);
        Instant now = Instant.now();
        newStudyClass.createdAt(now);
        newStudyClass.updatedAt(now);

        StudyClass savedClass = studyClassRepository.saveAndFlush(newStudyClass);
        return studyClassMapper.toStudyClassDetail(savedClass);
    }

    @Override
    public StudyClassDetailDTO updateClass(long id, UpdateStudyClassRequestDTO request) {
        Long currentUserId = requireInstructorId();
        StudyClass studyClass = studyClassRepository
            .findByIdForUpdate(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp học"));

        if (Boolean.TRUE.equals(studyClass.getIsDeleted())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp học");
        }
        if (!Objects.equals(studyClass.getInstructor().getId(), currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền sửa lớp học này");
        }
        if (
            request.getMaxStudents() != null &&
            request.getMaxStudents() < classMemberRepository.countByStudyClassIdAndStatus(id, ClassMemberStatus.ACTIVE.name())
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sĩ số tối đa nhỏ hơn số học sinh đang học");
        }

        studyClassMapper.updateStudyClass(studyClass, request);
        studyClass.setUpdatedAt(Instant.now());

        StudyClass savedClass = studyClassRepository.saveAndFlush(studyClass);
        return studyClassMapper.toStudyClassDetail(savedClass);
    }

    private String generateUniqueCode() {
        final String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        for (int attempt = 0; attempt < 20; attempt++) {
            StringBuilder candidate = new StringBuilder(8);
            for (int i = 0; i < 8; i++) {
                candidate.append(alphabet.charAt(secureRandom.nextInt(alphabet.length())));
            }
            String code = candidate.toString();
            if (!studyClassRepository.existsByJoinCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Không thể tạo mã lớp duy nhất");
    }
}
