package com.lumi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.lumi.domain.Assignment;
import com.lumi.domain.AssignmentStudent;
import com.lumi.domain.AssignmentSubmission;
import com.lumi.domain.Authority;
import com.lumi.domain.ClassMember;
import com.lumi.domain.StudyClass;
import com.lumi.domain.User;
import com.lumi.repository.AssignmentRepository;
import com.lumi.repository.AssignmentStudentRepository;
import com.lumi.repository.AssignmentSubmissionRepository;
import com.lumi.repository.ClassMemberRepository;
import com.lumi.repository.StudyClassRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.AuthoritiesConstants;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class ClassLearningProgressServiceTest {

    @Mock
    StudyClassRepository classes;

    @Mock
    UserRepository users;

    @Mock
    ClassMemberRepository members;

    @Mock
    AssignmentRepository assignments;

    @Mock
    AssignmentStudentRepository recipients;

    @Mock
    AssignmentSubmissionRepository submissions;

    @InjectMocks
    ClassLearningProgressService service;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void keepsAssignmentProgressAfterRemovingLessonCompletionStorage() {
        User instructor = new User();
        instructor.setId(7L);
        instructor.setLogin("instructor");
        instructor.setActivated(true);
        instructor.setAuthorities(Set.of(new Authority().name(AuthoritiesConstants.INSTRUCTOR)));
        User student = new User();
        student.setId(11L);
        StudyClass studyClass = new StudyClass().id(3L).instructor(instructor).isDeleted(false);
        Assignment assignment = new Assignment().id(9L).studyClass(studyClass).status("PUBLISHED").isDeleted(false);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("instructor", "unused"));
        when(users.findOneByLogin("instructor")).thenReturn(Optional.of(instructor));
        when(classes.findById(3L)).thenReturn(Optional.of(studyClass));
        when(members.findByStudyClassIdOrderByRequestedAtAsc(3L)).thenReturn(
            List.of(new ClassMember().studyClass(studyClass).student(student).status("ACTIVE"))
        );
        when(assignments.findByStudyClassIdAndIsDeletedFalseOrderByCreatedAtDesc(3L)).thenReturn(List.of(assignment));
        when(recipients.findByAssignmentStudyClassId(3L)).thenReturn(
            List.of(new AssignmentStudent().assignment(assignment).student(student))
        );
        when(submissions.findByAssignmentStudyClassId(3L)).thenReturn(
            List.of(new AssignmentSubmission().assignment(assignment).student(student).status("GRADED"))
        );

        var result = service.progress(3L);

        assertThat(result.students()).hasSize(1);
        assertThat(result.students().getFirst().submittedAssignments()).isEqualTo(1);
        assertThat(result.students().getFirst().gradedAssignments()).isEqualTo(1);
        assertThat(result.students().getFirst().totalAssignments()).isEqualTo(1);
    }
}
