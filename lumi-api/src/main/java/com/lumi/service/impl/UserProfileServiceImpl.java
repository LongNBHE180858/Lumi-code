package com.lumi.service.impl;

import com.lumi.domain.*;
import com.lumi.domain.enumeration.InstructorApplicationStatus;
import com.lumi.repository.*;
import com.lumi.security.SecurityUtils;
import com.lumi.service.UserProfileService;
import com.lumi.service.dto.*;
import com.lumi.service.mapper.UserProfileMapper;
import com.lumi.web.rest.errors.BadRequestAlertException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service Implementation for managing {@link com.lumi.domain.UserProfile}.
 */
@Service
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private static final Logger LOG = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    private final UserProfileRepository userProfileRepository;

    private final UserProfileMapper userProfileMapper;

    private final UserRepository userRepository;

    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final StudentStreakRepository studentStreakRepository;

    private final ParentStudentLinkRepository parentStudentLinkRepository;

    private final CourseRepository courseRepository;

    public UserProfileServiceImpl(
        UserProfileRepository userProfileRepository,
        UserProfileMapper userProfileMapper,
        UserRepository userRepository,
        CourseEnrollmentRepository courseEnrollmentRepository,
        StudentStreakRepository studentStreakRepository,
        ParentStudentLinkRepository parentStudentLinkRepository,
        CourseRepository courseRepository
    ) {
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
        this.userRepository = userRepository;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.studentStreakRepository = studentStreakRepository;
        this.parentStudentLinkRepository = parentStudentLinkRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public UserProfileDTO save(UserProfileDTO userProfileDTO) {
        LOG.debug("Request to save UserProfile : {}", userProfileDTO);
        UserProfile userProfile = userProfileMapper.toEntity(userProfileDTO);
        userProfile = userProfileRepository.save(userProfile);
        return userProfileMapper.toDto(userProfile);
    }

    @Override
    public UserProfileDTO update(UserProfileDTO userProfileDTO) {
        LOG.debug("Request to update UserProfile : {}", userProfileDTO);
        UserProfile userProfile = userProfileMapper.toEntity(userProfileDTO);
        userProfile = userProfileRepository.save(userProfile);
        return userProfileMapper.toDto(userProfile);
    }

    @Override
    public Optional<UserProfileDTO> partialUpdate(UserProfileDTO userProfileDTO) {
        LOG.debug("Request to partially update UserProfile : {}", userProfileDTO);

        return userProfileRepository
            .findById(userProfileDTO.getId())
            .map(existingUserProfile -> {
                userProfileMapper.partialUpdate(existingUserProfile, userProfileDTO);

                return existingUserProfile;
            })
            .map(userProfileRepository::save)
            .map(userProfileMapper::toDto);
    }

    public Page<UserProfileDTO> findAllWithEagerRelationships(Pageable pageable) {
        return userProfileRepository.findAllWithEagerRelationships(pageable).map(userProfileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfileDTO> findOne(Long id) {
        LOG.debug("Request to get UserProfile : {}", id);
        return userProfileRepository.findOneWithEagerRelationships(id).map(userProfileMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete UserProfile : {}", id);
        userProfileRepository.deleteById(id);
    }

    @Override
    public AccountProfileDTO getProfile() {
        //check và lấy id user
        Long userId = SecurityUtils.getCurrentUserId().orElseThrow(() ->
            new RuntimeException("Người dùng chưa đăng nhập hoặc không tìm thấy user ID trong phiên")
        );

        UserProfile userProfile = userProfileRepository
            .findUserByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy profile cho user id: " + userId));

        //lấy thông tin profile
        User user = userProfile.getUser();

        //logic kiểm tra trạng thái của giáo viên
        String verificationStatus = "UNVERIFIED";
        if (userProfile.getInstructorStatus() != null) {
            switch (userProfile.getInstructorStatus()) {
                case ACTIVE -> verificationStatus = "APPROVED";
                case PENDING -> verificationStatus = "PENDING";
                case REJECTED -> verificationStatus = "REJECTED";
                case BLOCKED -> verificationStatus = "BLOCKED";
                default -> verificationStatus = "UNVERIFIED";
            }
        }

        return AccountProfileDTO.builder()
            .id(user.getId())
            .login(user.getLogin())
            .email(user.getEmail())
            .birthDate(userProfile.getBirthDate())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .fullName(
                (user.getFirstName() != null ? user.getFirstName() + " " : "") + (user.getLastName() != null ? user.getLastName() : "")
            )
            .imageUrl(user.getImageUrl())
            .authorities(
                user.getAuthorities() != null
                    ? user
                          .getAuthorities()
                          .stream()
                          .map(authority -> authority.getName())
                          .collect(Collectors.toSet())
                    : null
            )
            // --- Thông tin lấy từ UserProfile ---
            .bio(userProfile.getBio())
            .phoneNumber(userProfile.getPhoneNumber())
            .schoolName(userProfile.getSchoolName())
            .gradeLevel(userProfile.getGradeLevel())
            .verificationStatus(verificationStatus)
            .build();
    }

    @Override
    public AccountProfileDTO updateProfile(AccountProfileDTO accountProfileDTO) {
        Long userId = SecurityUtils.getCurrentUserId().orElseThrow(() ->
            new RuntimeException("Người dùng chưa đăng nhập hoặc không tìm thấy user ID trong phiên")
        );
        UserProfile userProfile = userProfileRepository
            .findUserByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy profile cho user id: " + userId));
        User user = userProfile.getUser();
        String phoneNumber = accountProfileDTO.getPhoneNumber();
        if (phoneNumber != null) {
            phoneNumber = phoneNumber.replaceAll("[\\s-]", "");
            if (phoneNumber.isEmpty()) {
                phoneNumber = null;
            } else if (!phoneNumber.matches("^0[35789]\\d{8}$")) {
                throw new BadRequestAlertException(
                    //lỗi 400
                    "Số điện thoại phải có 10 chữ số, bắt đầu bằng 03, 05, 07, 08 hoặc 09.",
                    "userProfile",
                    "phoneinvalid"
                );
            }
        }
        if (phoneNumber != null && userProfileRepository.existsByPhoneNumberAndUserIdNot(phoneNumber, userId)) {
            throw new BadRequestAlertException("Số điện thoại đã được sử dụng", "userProfile", "phonealreadyused");
        }
        // cập nhật bảng User
        if (accountProfileDTO.getFirstName() != null) {
            user.setFirstName(accountProfileDTO.getFirstName());
        }
        if (accountProfileDTO.getLastName() != null) {
            user.setLastName(accountProfileDTO.getLastName());
        }
        if (accountProfileDTO.getImageUrl() != null) {
            user.setImageUrl(accountProfileDTO.getImageUrl());
        }
        // cập nhật bảng UserProfile
        if (accountProfileDTO.getBirthDate() != null) {
            userProfile.setBirthDate(accountProfileDTO.getBirthDate());
        }
        userProfile.setBio(accountProfileDTO.getBio());
        userProfile.setPhoneNumber(phoneNumber);
        userProfile.setSchoolName(accountProfileDTO.getSchoolName());
        userProfile.setGradeLevel(accountProfileDTO.getGradeLevel());
        userProfile.setUpdatedAt(java.time.Instant.now());

        userRepository.save(user);
        userProfileRepository.save(userProfile);
        return getProfile();
    }

    @Override
    public StudentProfileStatisticsDTO getProfileStatistic() {
        Long userId = SecurityUtils.getCurrentUserId().orElseThrow(() ->
            new RuntimeException("Người dùng chưa đăng nhập hoặc không tìm thấy user ID trong phiên")
        );

        long enrolledCourseCount = courseEnrollmentRepository.countDistinctCourseByStudentId(userId);
        StudentStreak streak = studentStreakRepository.findByStudent_Id(userId).orElse(null);

        return StudentProfileStatisticsDTO.builder()
            .enrolledCourseCount(enrolledCourseCount)
            .currentStreak(streak != null && streak.getCurrentStreak() != null ? streak.getCurrentStreak() : 0)
            .longestStreak(streak != null && streak.getLongestStreak() != null ? streak.getLongestStreak() : 0)
            .gemsBalance(streak != null && streak.getGemsBalance() != null ? streak.getGemsBalance() : 0)
            .build();
    }

    @Override
    public List<LinkedStudentDTO> getLinkedStudents() {
        Long parentId = SecurityUtils.getCurrentUserId().orElseThrow(() ->
            new RuntimeException("Người dùng chưa đăng nhập hoặc không tìm thấy user ID trong phiên")
        );

        List<ParentStudentLink> links = parentStudentLinkRepository.findByParentIdWithStudent(parentId);

        List<LinkedStudentDTO> result = new ArrayList<>();

        for (ParentStudentLink link : links) {
            if (!"LINKED".equals(link.getStatus())) {
                continue;
            }

            User student = link.getStudent();

            String firstName = student.getFirstName() != null ? student.getFirstName() : "";
            String lastName = student.getLastName() != null ? student.getLastName() : "";
            String fullName = (firstName + " " + lastName).trim();

            LinkedStudentDTO dto = LinkedStudentDTO.builder()
                .linkId(link.getId())
                .studentId(student.getId())
                .fullName(fullName)
                .status(link.getStatus())
                .build();

            result.add(dto);
        }

        return result;
    }

    @Override
    public InstructorPublicProfileDTO getPublicInstructorProfile(Long id) {
        UserProfile userProfile = userProfileRepository
            .findUserByUserId(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin giảng viên với id: " + id));

        User user = userProfile.getUser();

        boolean isInstructor =
            user.getAuthorities() != null &&
            user
                .getAuthorities()
                .stream()
                .anyMatch(a -> "ROLE_INSTRUCTOR".equals(a.getName()));

        if (!isInstructor || userProfile.getInstructorStatus() != InstructorApplicationStatus.ACTIVE || !user.isActivated()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hồ sơ giảng viên không tồn tại hoặc chưa được phê duyệt.");
        }
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";
        String fullName = (firstName + " " + lastName).trim();
        if (fullName.isEmpty()) {
            fullName = user.getLogin();
        }

        //Lấy danh sách Môn & Khối của giáo viên tổng hợp từ các khóa học ACTIVE
        List<TeachingScopeDTO> teachingScopes = courseRepository.findTeachingScopesByInstructorId(user.getId());

        return InstructorPublicProfileDTO.builder()
            .id(user.getId())
            .fullName(fullName)
            .imageUrl(user.getImageUrl())
            .bio(userProfile.getBio())
            .teachingScopes(teachingScopes)
            .build();
    }

    @Override
    public List<InstructorPublicCourseDTO> getInstructorPublicCourses(Long id) {
        UserProfile userProfile = userProfileRepository
            .findUserByUserId(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin giảng viên với id: " + id));

        User user = userProfile.getUser();

        boolean isInstructor =
            user.getAuthorities() != null &&
            user
                .getAuthorities()
                .stream()
                .anyMatch(a -> "ROLE_INSTRUCTOR".equals(a.getName()));

        if (!isInstructor || userProfile.getInstructorStatus() != InstructorApplicationStatus.ACTIVE || !user.isActivated()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hồ sơ giảng viên không tồn tại hoặc chưa được phê duyệt.");
        }

        Long instructorUserId = user.getId();

        //lấy danh sách khóa học ACTIVE của giảng viên
        List<Course> courses = courseRepository.findPublicCoursesByInstructorId(instructorUserId);

        //map sang InstructorPublicCourseDTO
        List<InstructorPublicCourseDTO> result = new ArrayList<>();
        for (Course c : courses) {
            InstructorPublicCourseDTO dto = InstructorPublicCourseDTO.builder()
                .id(c.getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .subjectId(c.getSubject() != null ? c.getSubject().getId() : null)
                .subjectName(c.getSubject() != null ? c.getSubject().getName() : null)
                .gradeId(c.getGrade() != null ? c.getGrade().getId() : null)
                .gradeLevel(c.getGrade() != null ? c.getGrade().getLevel() : null)
                .price(c.getPrice())
                .validDurationDays(c.getValidDurationDays())
                .build();

            result.add(dto);
        }

        return result;
    }
}
