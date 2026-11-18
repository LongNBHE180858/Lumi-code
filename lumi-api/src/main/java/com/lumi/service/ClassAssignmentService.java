package com.lumi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.domain.Assignment;
import com.lumi.domain.AssignmentSubmission;
import com.lumi.domain.ClassMember;
import com.lumi.domain.User;
import com.lumi.repository.AssignmentRepository;
import com.lumi.repository.AssignmentStudentRepository;
import com.lumi.repository.AssignmentSubmissionRepository;
import com.lumi.repository.ClassMemberRepository;
import com.lumi.repository.StudyClassRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.security.SecurityUtils;
import com.lumi.service.dto.studyClass.ClassAssignmentDTO;
import com.lumi.service.storage.ClassroomObjectStorage;
import com.lumi.service.storage.ClassroomStorageCleanup;
import com.lumi.service.storage.CourseFileValidator;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class ClassAssignmentService {

    private final AssignmentRepository assignments;
    private final AssignmentStudentRepository recipients;
    private final AssignmentSubmissionRepository submissions;
    private final ClassMemberRepository members;
    private final StudyClassRepository classes;
    private final UserRepository users;
    private final ClassroomObjectStorage fileStorage;
    private final ClassroomStorageCleanup storageCleanup;
    private final CourseFileValidator fileValidator;
    private final ObjectMapper json = new ObjectMapper();

    private record StoredAttachment(String url, String fileName, String fileType, long sizeBytes) {}

    public ClassAssignmentService(
        AssignmentRepository assignments,
        AssignmentStudentRepository recipients,
        AssignmentSubmissionRepository submissions,
        ClassMemberRepository members,
        StudyClassRepository classes,
        UserRepository users,
        ClassroomObjectStorage fileStorage,
        ClassroomStorageCleanup storageCleanup,
        CourseFileValidator fileValidator
    ) {
        this.assignments = assignments;
        this.recipients = recipients;
        this.submissions = submissions;
        this.members = members;
        this.classes = classes;
        this.users = users;
        this.fileStorage = fileStorage;
        this.storageCleanup = storageCleanup;
        this.fileValidator = fileValidator;
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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return user;
    }

    private Assignment assignment(Long classId, Long assignmentId, boolean lock) {
        Assignment assignment = (lock ? assignments.findByIdForUpdate(assignmentId) : assignments.findById(assignmentId)).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài tập")
        );
        if (
            !Objects.equals(assignment.getStudyClass().getId(), classId) ||
            Boolean.TRUE.equals(assignment.getIsDeleted()) ||
            Boolean.TRUE.equals(assignment.getStudyClass().getIsDeleted())
        ) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài tập");
        }
        return assignment;
    }

    private void ownedClass(Long classId, User instructor) {
        var studyClass = classes.findById(classId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (Boolean.TRUE.equals(studyClass.getIsDeleted()) || !Objects.equals(studyClass.getInstructor().getId(), instructor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private void activeStudent(Long classId, Long studentId) {
        var studyClass = classes.findById(classId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        ClassMember member = members
            .findByStudyClassIdAndStudentId(classId, studentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
        if (Boolean.TRUE.equals(studyClass.getIsDeleted()) || !"ACTIVE".equals(member.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private ClassAssignmentDTO.Submission dto(AssignmentSubmission item) {
        User student = item.getStudent();
        String name = (
            (student.getFirstName() == null ? "" : student.getFirstName()) +
            " " +
            (student.getLastName() == null ? "" : student.getLastName())
        ).trim();
        return new ClassAssignmentDTO.Submission(
            item.getId(),
            item.getAssignment().getId(),
            student.getId(),
            name.isBlank() ? student.getLogin() : name,
            item.getContent(),
            item.getStatus(),
            item.getScore(),
            item.getTeacherFeedback(),
            item.getSubmittedAt(),
            item.getGradedAt(),
            item.getRowVersion(),
            storedAttachments(item)
                .stream()
                .map(file -> new ClassAssignmentDTO.Attachment(file.fileName(), file.fileType(), file.sizeBytes()))
                .toList()
        );
    }

    private List<StoredAttachment> storedAttachments(AssignmentSubmission item) {
        if (item.getAttachments() == null || item.getAttachments().isBlank()) return List.of();
        try {
            return json.readValue(item.getAttachments(), new TypeReference<List<StoredAttachment>>() {});
        } catch (JsonProcessingException failure) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Dữ liệu tệp bài nộp không hợp lệ");
        }
    }

    private String attachmentJson(StoredAttachment attachment) {
        try {
            return json.writeValueAsString(List.of(attachment));
        } catch (JsonProcessingException failure) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không lưu được thông tin tệp bài nộp");
        }
    }

    private void assignedTo(Assignment assignment, User student) {
        activeStudent(assignment.getStudyClass().getId(), student.getId());
        if (
            !recipients.existsByAssignmentIdAndStudentId(assignment.getId(), student.getId()) || !"PUBLISHED".equals(assignment.getStatus())
        ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bài tập chưa được giao cho bạn");
        }
    }

    @Transactional(readOnly = true)
    public ClassroomObjectStorage.Access fileAccess(Long classId, Long assignmentId) {
        User user = currentUserForAssignment();
        Assignment assignment = assignment(classId, assignmentId, false);
        boolean owner = Objects.equals(assignment.getStudyClass().getInstructor().getId(), user.getId());
        if (!owner) assignedTo(assignment, user);
        if (assignment.getFileUrl() == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bài tập không có tệp đính kèm");
        return fileStorage.access(classId, assignment.getFileUrl(), assignment.getFileName());
    }

    private User currentUserForAssignment() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        User user = users.findOneByLogin(login).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!user.isActivated()) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return user;
    }

    @Transactional(readOnly = true)
    public ClassAssignmentDTO.Submission mine(Long classId, Long assignmentId) {
        User student = currentUser(AuthoritiesConstants.STUDENT);
        Assignment assignment = assignment(classId, assignmentId, false);
        assignedTo(assignment, student);
        return submissions
            .findByAssignmentIdAndStudentIdOrderByIdDesc(assignmentId, student.getId())
            .stream()
            .findFirst()
            .map(this::dto)
            .orElse(null);
    }

    public ClassAssignmentDTO.Submission submit(Long classId, Long assignmentId, ClassAssignmentDTO.Submit input) {
        return submitInternal(classId, assignmentId, input.content(), null);
    }

    public ClassAssignmentDTO.Submission submitWithFile(Long classId, Long assignmentId, String content, MultipartFile file) {
        return submitInternal(classId, assignmentId, content, file);
    }

    private ClassAssignmentDTO.Submission submitInternal(Long classId, Long assignmentId, String content, MultipartFile file) {
        User student = currentUser(AuthoritiesConstants.STUDENT);
        Assignment assignment = assignment(classId, assignmentId, true);
        assignedTo(assignment, student);
        Instant now = Instant.now();
        if (assignment.getDueAt() != null && now.isAfter(assignment.getDueAt()) && !Boolean.TRUE.equals(assignment.getAllowLate())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Đã hết hạn nộp bài");
        }
        if (!submissions.findByAssignmentIdAndStudentIdOrderByIdDesc(assignmentId, student.getId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bài tập đã được nộp");
        }
        String answer = content == null ? "" : content.trim();
        if (answer.length() > 10000 || (answer.isEmpty() && (file == null || file.isEmpty()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hãy nhập câu trả lời hoặc chọn tệp bài làm");
        }
        String attachmentsJson = "[]";
        if (file != null) {
            String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase(Locale.ROOT);
            if (!name.endsWith(".pdf") && !name.endsWith(".docx")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ hỗ trợ tệp PDF hoặc DOCX");
            }
            if (file.getSize() > 25 * 1024 * 1024L) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tệp bài làm vượt quá dung lượng tối đa 25 MB");
            }
            try (var validated = fileValidator.validate(file, CourseFileValidator.Kind.DOCUMENT)) {
                var uploaded = fileStorage.upload(
                    classId,
                    CourseFileValidator.Kind.DOCUMENT,
                    validated.path(),
                    validated.name(),
                    validated.mimeType()
                );
                storageCleanup.onRollback(classId, uploaded.url());
                String fileType = validated.mimeType().equals("application/pdf") ? "PDF" : "DOCX";
                attachmentsJson = attachmentJson(new StoredAttachment(uploaded.url(), uploaded.fileName(), fileType, uploaded.size()));
            } catch (IOException failure) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không xử lý được tệp bài làm");
            }
        }
        AssignmentSubmission saved = submissions.save(
            new AssignmentSubmission()
                .assignment(assignment)
                .student(student)
                .content(answer)
                .status("SUBMITTED")
                .submittedAt(now)
                .updatedAt(now)
                .attachments(attachmentsJson)
                .rowVersion(0L)
        );
        return dto(saved);
    }

    @Transactional(readOnly = true)
    public ClassroomObjectStorage.Access submissionFileAccess(Long classId, Long assignmentId, Long submissionId) {
        User user = currentUserForAssignment();
        Assignment assignment = assignment(classId, assignmentId, false);
        AssignmentSubmission submission = submissions
            .findById(submissionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!Objects.equals(submission.getAssignment().getId(), assignmentId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        boolean instructor = Objects.equals(assignment.getStudyClass().getInstructor().getId(), user.getId());
        if (!instructor) {
            if (!Objects.equals(submission.getStudent().getId(), user.getId())) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            assignedTo(assignment, user);
        }
        StoredAttachment file = storedAttachments(submission)
            .stream()
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return fileStorage.access(classId, file.url(), file.fileName());
    }

    @Transactional(readOnly = true)
    public ClassAssignmentDTO.SubmissionList instructorSubmissions(Long classId, Long assignmentId) {
        User instructor = currentUser(AuthoritiesConstants.INSTRUCTOR);
        ownedClass(classId, instructor);
        assignment(classId, assignmentId, false);
        List<ClassAssignmentDTO.Submission> items = submissions
            .findByAssignmentIdOrderBySubmittedAtDescIdDesc(assignmentId)
            .stream()
            .map(this::dto)
            .toList();
        Set<Long> activeStudentIds = members
            .findByStudyClassIdOrderByRequestedAtAsc(classId)
            .stream()
            .filter(member -> "ACTIVE".equals(member.getStatus()))
            .map(member -> member.getStudent().getId())
            .collect(Collectors.toSet());
        long assignedCount = recipients
            .findByAssignmentId(assignmentId)
            .stream()
            .map(recipient -> recipient.getStudent().getId())
            .filter(activeStudentIds::contains)
            .distinct()
            .count();
        return new ClassAssignmentDTO.SubmissionList(items, assignedCount);
    }

    public ClassAssignmentDTO.Submission grade(Long classId, Long assignmentId, Long submissionId, ClassAssignmentDTO.Grade input) {
        User instructor = currentUser(AuthoritiesConstants.INSTRUCTOR);
        ownedClass(classId, instructor);
        assignment(classId, assignmentId, true);
        AssignmentSubmission item = submissions.findById(submissionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (
            !Objects.equals(item.getAssignment().getId(), assignmentId) ||
            !recipients.existsByAssignmentIdAndStudentId(assignmentId, item.getStudent().getId())
        ) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (!Objects.equals(item.getRowVersion(), input.version())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bài nộp đã thay đổi; hãy tải lại");
        }
        item.setScore(input.score());
        item.setTeacherFeedback(input.feedback() == null ? null : input.feedback().trim());
        item.setStatus("GRADED");
        item.setGradedAt(Instant.now());
        item.setUpdatedAt(Instant.now());
        item.setGradedBy(instructor);
        item.setRowVersion(item.getRowVersion() + 1);
        return dto(submissions.save(item));
    }
}
