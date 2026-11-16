package com.lumi.service;

import com.lumi.domain.Assignment;
import com.lumi.domain.AssignmentStudent;
import com.lumi.domain.ClassMaterial;
import com.lumi.domain.ClassMember;
import com.lumi.domain.StudyClass;
import com.lumi.domain.User;
import com.lumi.domain.enumeration.ClassJoinMode;
import com.lumi.domain.enumeration.ClassMemberStatus;
import com.lumi.domain.enumeration.RecordStatus;
import com.lumi.repository.AssignmentRepository;
import com.lumi.repository.AssignmentStudentRepository;
import com.lumi.repository.ClassMaterialRepository;
import com.lumi.repository.ClassMemberRepository;
import com.lumi.repository.StudyClassRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.security.SecurityUtils;
import com.lumi.service.dto.studyClass.ClassroomDTO;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class ClassroomService {

    private final StudyClassRepository classes;
    private final ClassMemberRepository members;
    private final ClassMaterialRepository materials;
    private final AssignmentRepository assignments;
    private final AssignmentStudentRepository recipients;
    private final UserRepository users;

    public ClassroomService(
        StudyClassRepository classes,
        ClassMemberRepository members,
        ClassMaterialRepository materials,
        AssignmentRepository assignments,
        AssignmentStudentRepository recipients,
        UserRepository users
    ) {
        this.classes = classes;
        this.members = members;
        this.materials = materials;
        this.assignments = assignments;
        this.recipients = recipients;
        this.users = users;
    }

    private User currentUser(String authority) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        User user = users.findOneByLogin(login).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (
            !user.isActivated() ||
            user
                .getAuthorities()
                .stream()
                .noneMatch(role -> authority.equals(role.getName()))
        ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền truy cập lớp học");
        }
        return user;
    }

    private StudyClass activeClass(Long id) {
        StudyClass studyClass = classes
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp"));
        if (Boolean.TRUE.equals(studyClass.getIsDeleted())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp");
        }
        return studyClass;
    }

    private StudyClass ownedClass(Long id, User instructor) {
        StudyClass studyClass = activeClass(id);
        if (!Objects.equals(studyClass.getInstructor().getId(), instructor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không quản lý lớp này");
        }
        return studyClass;
    }

    private ClassroomDTO.Task task(Assignment assignment) {
        return new ClassroomDTO.Task(
            assignment.getId(),
            assignment.getTitle(),
            assignment.getInstructions(),
            assignment.getDueAt(),
            assignment.getStatus()
        );
    }

    private ClassroomDTO.Material material(ClassMaterial item) {
        return new ClassroomDTO.Material(
            item.getId(),
            item.getTitle(),
            item.getFileType(),
            item.getFileUrl(),
            item.getUploadedAt(),
            item.getOrderIndex()
        );
    }

    private ClassroomDTO.Member member(ClassMember item) {
        User student = item.getStudent();
        String name = (
            (student.getFirstName() == null ? "" : student.getFirstName()) +
            " " +
            (student.getLastName() == null ? "" : student.getLastName())
        ).trim();
        return new ClassroomDTO.Member(
            item.getId(),
            student.getId(),
            name.isBlank() ? student.getLogin() : name,
            student.getEmail(),
            item.getStatus(),
            item.getJoinedAt()
        );
    }

    @Transactional(readOnly = true)
    public ClassroomDTO instructorClass(Long id) {
        StudyClass studyClass = ownedClass(id, currentUser(AuthoritiesConstants.INSTRUCTOR));
        return new ClassroomDTO(
            id,
            studyClass.getName(),
            studyClass.getDescription(),
            studyClass.getJoinCode(),
            List.of(),
            materials.findByStudyClassIdOrderByOrderIndexAscIdAsc(id).stream().map(this::material).toList(),
            assignments.findByStudyClassIdAndIsDeletedFalseOrderByCreatedAtDesc(id).stream().map(this::task).toList()
        );
    }

    @Transactional(readOnly = true)
    public ClassroomDTO.MemberPage instructorMembers(Long classId, int page, int size) {
        ownedClass(classId, currentUser(AuthoritiesConstants.INSTRUCTOR));
        Page<ClassMember> result = members.findVisibleClassMembers(
            classId,
            PageRequest.of(
                Math.max(0, page),
                Math.max(1, Math.min(size, 50)),
                Sort.by(Sort.Order.desc("status"), Sort.Order.asc("requestedAt"), Sort.Order.asc("id"))
            )
        );
        return new ClassroomDTO.MemberPage(
            result.getContent().stream().map(this::member).toList(),
            result.getNumber(),
            result.getTotalElements(),
            result.getTotalPages(),
            members.countByStudyClassIdAndStatus(classId, ClassMemberStatus.ACTIVE.name()),
            members.countByStudyClassIdAndStatus(classId, ClassMemberStatus.PENDING.name())
        );
    }

    public ClassroomDTO.Member approveMember(Long classId, Long memberId) {
        User instructor = currentUser(AuthoritiesConstants.INSTRUCTOR);
        StudyClass studyClass = classes
            .findByIdForUpdate(classId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp"));
        if (Boolean.TRUE.equals(studyClass.getIsDeleted()) || !Objects.equals(studyClass.getInstructor().getId(), instructor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không quản lý lớp này");
        }
        if (studyClass.getStatus() != RecordStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lớp chưa mở tham gia");
        }
        ClassMember member = members
            .findById(memberId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy học sinh"));
        if (!Objects.equals(member.getStudyClass().getId(), classId) || !ClassMemberStatus.PENDING.name().equals(member.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Yêu cầu tham gia không hợp lệ");
        }
        long count = members.countByStudyClassIdAndStatus(classId, ClassMemberStatus.ACTIVE.name());
        if (studyClass.getMaxStudents() != null && count >= studyClass.getMaxStudents()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lớp đã đủ sĩ số");
        }
        member.setStatus(ClassMemberStatus.ACTIVE.name());
        member.setJoinedAt(Instant.now());
        member.setApprovedAt(Instant.now());
        member.setUpdatedAt(Instant.now());
        addExistingAssignments(member);
        return member(members.save(member));
    }

    public void removeMember(Long classId, Long memberId, boolean pendingOnly) {
        ownedClass(classId, currentUser(AuthoritiesConstants.INSTRUCTOR));
        ClassMember member = members
            .findById(memberId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy học sinh"));
        if (
            !Objects.equals(member.getStudyClass().getId(), classId) ||
            (pendingOnly && !ClassMemberStatus.PENDING.name().equals(member.getStatus())) ||
            (!pendingOnly && !ClassMemberStatus.ACTIVE.name().equals(member.getStatus()))
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thành viên không ở trạng thái phù hợp");
        }
        member.setStatus(ClassMemberStatus.REMOVED.name());
        member.setDecisionReason(pendingOnly ? "Giáo viên từ chối yêu cầu tham gia" : "Giáo viên đưa học sinh ra khỏi lớp");
        member.setLeftAt(Instant.now());
        member.setUpdatedAt(Instant.now());
        members.save(member);
    }

    public ClassroomDTO.Task createTask(Long classId, ClassroomDTO.CreateTaskRequest request) {
        User instructor = currentUser(AuthoritiesConstants.INSTRUCTOR);
        StudyClass studyClass = ownedClass(classId, instructor);
        Instant now = Instant.now();
        Assignment assignment = new Assignment()
            .title(request.title().trim())
            .instructions(request.instructions())
            .dueAt(request.dueAt())
            .allowLate(false)
            .isDeleted(false)
            .status("PUBLISHED")
            .createdAt(now)
            .updatedAt(now)
            .studyClass(studyClass)
            .assignedBy(instructor);
        assignment = assignments.save(assignment);
        for (ClassMember member : members.findByStudyClassIdOrderByRequestedAtAsc(classId)) {
            if (ClassMemberStatus.ACTIVE.name().equals(member.getStatus())) {
                recipients.save(new AssignmentStudent().assignment(assignment).student(member.getStudent()));
            }
        }
        return task(assignment);
    }

    @Transactional(readOnly = true)
    public ClassroomDTO.JoinedClassPage studentClasses(int page, int size) {
        User student = currentUser(AuthoritiesConstants.STUDENT);
        Page<ClassMember> result = members.findVisibleStudentClasses(
            student.getId(),
            PageRequest.of(
                Math.max(0, page),
                Math.max(1, Math.min(size, 50)),
                Sort.by(Sort.Order.desc("requestedAt"), Sort.Order.desc("id"))
            )
        );
        return new ClassroomDTO.JoinedClassPage(
            result
                .getContent()
                .stream()
                .map(item ->
                    new ClassroomDTO.JoinedClass(
                        item.getStudyClass().getId(),
                        item.getStudyClass().getName(),
                        item.getStudyClass().getDescription(),
                        item.getStatus()
                    )
                )
                .toList(),
            result.getNumber(),
            result.getTotalElements(),
            result.getTotalPages()
        );
    }

    public ClassroomDTO.JoinedClass joinClass(String code) {
        User student = currentUser(AuthoritiesConstants.STUDENT);
        StudyClass studyClass = classes
            .findByJoinCodeIgnoreCase(code.trim())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mã lớp không tồn tại"));
        if (Boolean.TRUE.equals(studyClass.getIsDeleted()) || studyClass.getStatus() != RecordStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lớp chưa mở tham gia");
        }
        if (studyClass.getJoinMode() == ClassJoinMode.INVITE_ONLY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lớp chỉ nhận lời mời");
        }
        if (studyClass.getJoinCodeExpiresAt() != null && studyClass.getJoinCodeExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã lớp đã hết hạn");
        }
        ClassMember member = members.findByStudyClassIdAndStudentId(studyClass.getId(), student.getId()).orElse(null);
        if (member != null && !ClassMemberStatus.REMOVED.name().equals(member.getStatus())) {
            return new ClassroomDTO.JoinedClass(studyClass.getId(), studyClass.getName(), studyClass.getDescription(), member.getStatus());
        }
        boolean autoJoin = studyClass.getJoinMode() == ClassJoinMode.CODE_AUTO;
        if (
            studyClass.getMaxStudents() != null &&
            members.countByStudyClassIdAndStatus(studyClass.getId(), ClassMemberStatus.ACTIVE.name()) >= studyClass.getMaxStudents()
        ) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lớp đã đủ sĩ số");
        }
        Instant now = Instant.now();
        if (member == null) {
            member = new ClassMember().studyClass(studyClass).student(student);
        }
        member.setStatus(autoJoin ? ClassMemberStatus.ACTIVE.name() : ClassMemberStatus.PENDING.name());
        member.setRequestedAt(now);
        member.setAppliedAt(now);
        member.setJoinedAt(autoJoin ? now : null);
        member.setApprovedAt(autoJoin ? now : null);
        member.setDecisionReason(null);
        member.setLeftAt(null);
        member.setUpdatedAt(now);
        members.save(member);
        if (autoJoin) addExistingAssignments(member);
        return new ClassroomDTO.JoinedClass(studyClass.getId(), studyClass.getName(), studyClass.getDescription(), member.getStatus());
    }

    private void addExistingAssignments(ClassMember member) {
        for (Assignment assignment : assignments.findByStudyClassIdAndIsDeletedFalseOrderByCreatedAtDesc(member.getStudyClass().getId())) {
            if (!recipients.existsByAssignmentIdAndStudentId(assignment.getId(), member.getStudent().getId())) {
                recipients.save(new AssignmentStudent().assignment(assignment).student(member.getStudent()));
            }
        }
    }

    @Transactional(readOnly = true)
    public ClassroomDTO studentClass(Long id) {
        User student = currentUser(AuthoritiesConstants.STUDENT);
        StudyClass studyClass = activeClass(id);
        ClassMember membership = members
            .findByStudyClassIdAndStudentId(id, student.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn chưa tham gia lớp"));
        if (!ClassMemberStatus.ACTIVE.name().equals(membership.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn chưa được duyệt vào lớp");
        }
        return new ClassroomDTO(
            id,
            studyClass.getName(),
            studyClass.getDescription(),
            null,
            List.of(),
            materials
                .findByStudyClassIdOrderByOrderIndexAscIdAsc(id)
                .stream()
                .filter(item -> "PUBLISHED".equals(item.getStatus()))
                .map(this::material)
                .toList(),
            assignments
                .findByStudyClassIdAndIsDeletedFalseOrderByCreatedAtDesc(id)
                .stream()
                .filter(item -> "PUBLISHED".equals(item.getStatus()))
                .map(this::task)
                .toList()
        );
    }
}
