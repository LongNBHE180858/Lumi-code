package com.lumi.domain;

import com.lumi.domain.enumeration.InstructorApplicationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Lưu hồ sơ mở rộng, thông tin trường học và trạng thái giáo viên của người dùng.
 */
@Entity
@Table(name = "user_profiles")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserProfile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 20)
    @Column(name = "phone_number", length = 20, unique = true)
    private String phoneNumber;

    @Column(name = "bio", columnDefinition = "text")
    private String bio;

    @Size(max = 255)
    @Column(name = "school_name", length = 255)
    private String schoolName;

    @Min(value = 1)
    @Max(value = 12)
    @Column(name = "grade_level")
    private Integer gradeLevel;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "is_2_fa_enabled")
    private Boolean is2faEnabled;

    @Enumerated(EnumType.STRING)
    @Column(name = "instructor_status")
    private InstructorApplicationStatus instructorStatus;

    @Size(max = 2000)
    @Column(name = "instructor_rejection_reason", length = 2000)
    private String instructorRejectionReason;

    @Column(name = "instructor_reviewed_at")
    private Instant instructorReviewedAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserProfile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public UserProfile phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBio() {
        return this.bio;
    }

    public UserProfile bio(String bio) {
        this.setBio(bio);
        return this;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSchoolName() {
        return this.schoolName;
    }

    public UserProfile schoolName(String schoolName) {
        this.setSchoolName(schoolName);
        return this;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public Integer getGradeLevel() {
        return this.gradeLevel;
    }

    public UserProfile gradeLevel(Integer gradeLevel) {
        this.setGradeLevel(gradeLevel);
        return this;
    }

    public void setGradeLevel(Integer gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Boolean getIs2faEnabled() {
        return this.is2faEnabled;
    }

    public UserProfile is2faEnabled(Boolean is2faEnabled) {
        this.setIs2faEnabled(is2faEnabled);
        return this;
    }

    public void setIs2faEnabled(Boolean is2faEnabled) {
        this.is2faEnabled = is2faEnabled;
    }

    public InstructorApplicationStatus getInstructorStatus() {
        return this.instructorStatus;
    }

    public UserProfile instructorStatus(InstructorApplicationStatus instructorStatus) {
        this.setInstructorStatus(instructorStatus);
        return this;
    }

    public void setInstructorStatus(InstructorApplicationStatus instructorStatus) {
        this.instructorStatus = instructorStatus;
    }

    public String getInstructorRejectionReason() {
        return instructorRejectionReason;
    }

    public void setInstructorRejectionReason(String instructorRejectionReason) {
        this.instructorRejectionReason = instructorRejectionReason;
    }

    public Instant getInstructorReviewedAt() {
        return instructorReviewedAt;
    }

    public void setInstructorReviewedAt(Instant instructorReviewedAt) {
        this.instructorReviewedAt = instructorReviewedAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public UserProfile updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public UserProfile birthDate(LocalDate birthDate) {
        this.setBirthDate(birthDate);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserProfile user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserProfile)) {
            return false;
        }
        return getId() != null && getId().equals(((UserProfile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserProfile{" +
            "id=" + getId() +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", bio='" + getBio() + "'" +
            ", schoolName='" + getSchoolName() + "'" +
            ", gradeLevel=" + getGradeLevel() +
            ", is2faEnabled='" + getIs2faEnabled() + "'" +
            ", instructorStatus='" + getInstructorStatus() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            "}";
    }
}
