package com.lumi.service.dto.auth;

import com.lumi.domain.UserProfile;
import com.lumi.domain.enumeration.InstructorApplicationStatus;
import java.io.Serializable;
import java.time.LocalDate;

public class UserProfileAuthDTO implements Serializable {

    private Long id;
    private String phoneNumber;
    private String bio;
    private String schoolName;
    private Integer gradeLevel;
    private LocalDate birthDate;
    private Boolean is2faEnabled;
    private InstructorApplicationStatus instructorStatus;

    public UserProfileAuthDTO() {}

    public UserProfileAuthDTO(UserProfile profile) {
        if (profile != null) {
            this.id = profile.getId();
            this.phoneNumber = profile.getPhoneNumber();
            this.bio = profile.getBio();
            this.schoolName = profile.getSchoolName();
            this.gradeLevel = profile.getGradeLevel();
            this.birthDate = profile.getBirthDate();
            this.is2faEnabled = profile.getIs2faEnabled();
            this.instructorStatus = profile.getInstructorStatus();
        }
    }

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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
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
}
