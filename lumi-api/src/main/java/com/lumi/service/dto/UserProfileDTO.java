package com.lumi.service.dto;

import com.lumi.domain.enumeration.InstructorApplicationStatus;
import com.lumi.domain.enumeration.RecordStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.UserProfile} entity.
 */
@Schema(description = "Lưu hồ sơ mở rộng, thông tin trường học và trạng thái giáo viên của người dùng.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserProfileDTO implements Serializable {

    private Long id;

    @Size(max = 20)
    private String phoneNumber;

    @Lob
    private String bio;

    @Size(max = 255)
    private String schoolName;

    @Min(value = 1)
    @Max(value = 12)
    private Integer gradeLevel;

    private LocalDate birthDate;

    private Boolean is2faEnabled;

    private InstructorApplicationStatus instructorStatus;

    private Instant updatedAt;

    @NotNull
    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public Integer getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(Integer gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public Boolean getIs2faEnabled() {
        return is2faEnabled;
    }

    public void setIs2faEnabled(Boolean is2faEnabled) {
        this.is2faEnabled = is2faEnabled;
    }

    public InstructorApplicationStatus getInstructorStatus() {
        return instructorStatus;
    }

    public void setInstructorStatus(InstructorApplicationStatus instructorStatus) {
        this.instructorStatus = instructorStatus;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserProfileDTO)) {
            return false;
        }

        UserProfileDTO userProfileDTO = (UserProfileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userProfileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserProfileDTO{" +
            "id=" + getId() +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", bio='" + getBio() + "'" +
            ", schoolName='" + getSchoolName() + "'" +
            ", gradeLevel=" + getGradeLevel() +
            ", is2faEnabled='" + getIs2faEnabled() + "'" +
            ", instructorStatus='" + getInstructorStatus() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
