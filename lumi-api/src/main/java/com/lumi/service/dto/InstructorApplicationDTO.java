package com.lumi.service.dto;

import com.lumi.domain.User;
import com.lumi.domain.UserProfile;
import com.lumi.domain.enumeration.InstructorApplicationStatus;
import java.time.Instant;

/** Information from an instructor registration and its review decision. */
public record InstructorApplicationDTO(
    Long userId,
    String login,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String schoolName,
    Integer gradeLevel,
    boolean activated,
    InstructorApplicationStatus status,
    Instant submittedAt,
    Instant reviewedAt,
    String rejectionReason
) {
    public static InstructorApplicationDTO from(UserProfile profile) {
        User user = profile.getUser();
        return new InstructorApplicationDTO(
            user.getId(),
            user.getLogin(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            profile.getPhoneNumber(),
            profile.getSchoolName(),
            profile.getGradeLevel(),
            user.isActivated(),
            profile.getInstructorStatus(),
            user.getCreatedDate(),
            profile.getInstructorReviewedAt(),
            profile.getInstructorRejectionReason()
        );
    }
}
