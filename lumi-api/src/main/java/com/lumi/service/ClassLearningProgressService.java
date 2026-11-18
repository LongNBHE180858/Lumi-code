package com.lumi.service;

import com.lumi.domain.Assignment;
import com.lumi.domain.AssignmentStudent;
import com.lumi.domain.AssignmentSubmission;
import com.lumi.domain.User;
import com.lumi.repository.AssignmentRepository;
import com.lumi.repository.AssignmentStudentRepository;
import com.lumi.repository.AssignmentSubmissionRepository;
import com.lumi.repository.ClassMemberRepository;
import com.lumi.repository.StudyClassRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.security.SecurityUtils;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class ClassLearningProgressService {

    public record StudentProgress(Long studentId, int submittedAssignments, int gradedAssignments, int totalAssignments) {}

    public record Progress(List<StudentProgress> students) {}

    private final StudyClassRepository classes;
    private final UserRepository users;
    private final ClassMemberRepository members;
    private final AssignmentRepository assignments;
    private final AssignmentStudentRepository recipients;
    private final AssignmentSubmissionRepository submissions;

    public ClassLearningProgressService(
        StudyClassRepository classes,
        UserRepository users,
        ClassMemberRepository members,
        AssignmentRepository assignments,
        AssignmentStudentRepository recipients,
        AssignmentSubmissionRepository submissions
    ) {
        this.classes = classes;
        this.users = users;
        this.members = members;
        this.assignments = assignments;
        this.recipients = recipients;
        this.submissions = submissions;
    }

    public Progress progress(Long classId) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        User instructor = users.findOneByLogin(login).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        var studyClass = classes.findById(classId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (
            !instructor.isActivated() ||
            instructor
                .getAuthorities()
                .stream()
                .noneMatch(a -> AuthoritiesConstants.INSTRUCTOR.equals(a.getName())) ||
            Boolean.TRUE.equals(studyClass.getIsDeleted()) ||
            !Objects.equals(studyClass.getInstructor().getId(), instructor.getId())
        ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        List<Assignment> classAssignments = assignments
            .findByStudyClassIdAndIsDeletedFalseOrderByCreatedAtDesc(classId)
            .stream()
            .filter(a -> "PUBLISHED".equals(a.getStatus()))
            .toList();
        List<AssignmentStudent> assignedRows = recipients.findByAssignmentStudyClassId(classId);
        List<AssignmentSubmission> submitted = submissions.findByAssignmentStudyClassId(classId);
        List<StudentProgress> students = members
            .findByStudyClassIdOrderByRequestedAtAsc(classId)
            .stream()
            .filter(m -> "ACTIVE".equals(m.getStatus()))
            .map(member -> {
                Long studentId = member.getStudent().getId();
                List<Assignment> assigned = classAssignments
                    .stream()
                    .filter(a ->
                        assignedRows
                            .stream()
                            .anyMatch(
                                row ->
                                    Objects.equals(row.getStudent().getId(), studentId) &&
                                    Objects.equals(row.getAssignment().getId(), a.getId())
                            )
                    )
                    .toList();
                List<AssignmentSubmission> own = submitted
                    .stream()
                    .filter(s -> Objects.equals(s.getStudent().getId(), studentId))
                    .filter(s -> assigned.stream().anyMatch(a -> Objects.equals(a.getId(), s.getAssignment().getId())))
                    .toList();
                return new StudentProgress(
                    studentId,
                    own.size(),
                    (int) own
                        .stream()
                        .filter(s -> "GRADED".equals(s.getStatus()))
                        .count(),
                    assigned.size()
                );
            })
            .toList();
        return new Progress(students);
    }
}
