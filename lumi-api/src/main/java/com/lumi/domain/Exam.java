package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lumi.domain.enumeration.RecordStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Lưu đề đánh giá, bài luyện, checkpoint video và cấu hình tổ chức làm bài.
 */
@Entity
@Table(name = "exams")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Exam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @NotNull
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @NotNull
    @Column(name = "pass_score", precision = 21, scale = 2, nullable = false)
    private BigDecimal passScore;

    @Column(name = "max_attempts")
    private Integer maxAttempts;

    @Column(name = "shuffle_questions")
    private Boolean shuffleQuestions;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RecordStatus status;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "opens_at")
    private Instant opensAt;

    @Column(name = "closes_at")
    private Instant closesAt;

    @NotNull
    @Size(max = 20)
    @Column(name = "result_release_mode", length = 20, nullable = false)
    private String resultReleaseMode;

    @NotNull
    @Size(max = 20)
    @Column(name = "answer_release_mode", length = 20, nullable = false)
    private String answerReleaseMode;

    @NotNull
    @Column(name = "version", nullable = false)
    private Long version;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Size(max = 30)
    @Column(name = "assessment_type", length = 30, nullable = false)
    private String assessmentType;

    @DecimalMin(value = "0")
    @Column(name = "video_trigger_seconds", precision = 21, scale = 2)
    private BigDecimal videoTriggerSeconds;

    @Column(name = "assessment_config", nullable = false, columnDefinition = "text")
    private String assessmentConfig;

    @Column(name = "security_config", nullable = false, columnDefinition = "text")
    private String securityConfig;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "instructor", "subject", "grade" }, allowSetters = true)
    private StudyClass studyClass;

    @ManyToOne(optional = false)
    @NotNull
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "chapter" }, allowSetters = true)
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    private User assignedStudent;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Exam id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Exam title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDurationMinutes() {
        return this.durationMinutes;
    }

    public Exam durationMinutes(Integer durationMinutes) {
        this.setDurationMinutes(durationMinutes);
        return this;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getPassScore() {
        return this.passScore;
    }

    public Exam passScore(BigDecimal passScore) {
        this.setPassScore(passScore);
        return this;
    }

    public void setPassScore(BigDecimal passScore) {
        this.passScore = passScore;
    }

    public Integer getMaxAttempts() {
        return this.maxAttempts;
    }

    public Exam maxAttempts(Integer maxAttempts) {
        this.setMaxAttempts(maxAttempts);
        return this;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Boolean getShuffleQuestions() {
        return this.shuffleQuestions;
    }

    public Exam shuffleQuestions(Boolean shuffleQuestions) {
        this.setShuffleQuestions(shuffleQuestions);
        return this;
    }

    public void setShuffleQuestions(Boolean shuffleQuestions) {
        this.shuffleQuestions = shuffleQuestions;
    }

    public RecordStatus getStatus() {
        return this.status;
    }

    public Exam status(RecordStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public Exam isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getOpensAt() {
        return this.opensAt;
    }

    public Exam opensAt(Instant opensAt) {
        this.setOpensAt(opensAt);
        return this;
    }

    public void setOpensAt(Instant opensAt) {
        this.opensAt = opensAt;
    }

    public Instant getClosesAt() {
        return this.closesAt;
    }

    public Exam closesAt(Instant closesAt) {
        this.setClosesAt(closesAt);
        return this;
    }

    public void setClosesAt(Instant closesAt) {
        this.closesAt = closesAt;
    }

    public String getResultReleaseMode() {
        return this.resultReleaseMode;
    }

    public Exam resultReleaseMode(String resultReleaseMode) {
        this.setResultReleaseMode(resultReleaseMode);
        return this;
    }

    public void setResultReleaseMode(String resultReleaseMode) {
        this.resultReleaseMode = resultReleaseMode;
    }

    public String getAnswerReleaseMode() {
        return this.answerReleaseMode;
    }

    public Exam answerReleaseMode(String answerReleaseMode) {
        this.setAnswerReleaseMode(answerReleaseMode);
        return this;
    }

    public void setAnswerReleaseMode(String answerReleaseMode) {
        this.answerReleaseMode = answerReleaseMode;
    }

    public Long getVersion() {
        return this.version;
    }

    public Exam version(Long version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Exam createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getAssessmentType() {
        return this.assessmentType;
    }

    public Exam assessmentType(String assessmentType) {
        this.setAssessmentType(assessmentType);
        return this;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public BigDecimal getVideoTriggerSeconds() {
        return this.videoTriggerSeconds;
    }

    public Exam videoTriggerSeconds(BigDecimal videoTriggerSeconds) {
        this.setVideoTriggerSeconds(videoTriggerSeconds);
        return this;
    }

    public void setVideoTriggerSeconds(BigDecimal videoTriggerSeconds) {
        this.videoTriggerSeconds = videoTriggerSeconds;
    }

    public String getAssessmentConfig() {
        return this.assessmentConfig;
    }

    public Exam assessmentConfig(String assessmentConfig) {
        this.setAssessmentConfig(assessmentConfig);
        return this;
    }

    public void setAssessmentConfig(String assessmentConfig) {
        this.assessmentConfig = assessmentConfig;
    }

    public String getSecurityConfig() {
        return this.securityConfig;
    }

    public Exam securityConfig(String securityConfig) {
        this.setSecurityConfig(securityConfig);
        return this;
    }

    public void setSecurityConfig(String securityConfig) {
        this.securityConfig = securityConfig;
    }

    public StudyClass getStudyClass() {
        return this.studyClass;
    }

    public void setStudyClass(StudyClass studyClass) {
        this.studyClass = studyClass;
    }

    public Exam studyClass(StudyClass studyClass) {
        this.setStudyClass(studyClass);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public Exam createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public Lesson getLesson() {
        return this.lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public Exam lesson(Lesson lesson) {
        this.setLesson(lesson);
        return this;
    }

    public User getAssignedStudent() {
        return this.assignedStudent;
    }

    public void setAssignedStudent(User user) {
        this.assignedStudent = user;
    }

    public Exam assignedStudent(User user) {
        this.setAssignedStudent(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Exam)) {
            return false;
        }
        return getId() != null && getId().equals(((Exam) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Exam{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", durationMinutes=" + getDurationMinutes() +
            ", passScore=" + getPassScore() +
            ", maxAttempts=" + getMaxAttempts() +
            ", shuffleQuestions='" + getShuffleQuestions() + "'" +
            ", status='" + getStatus() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", opensAt='" + getOpensAt() + "'" +
            ", closesAt='" + getClosesAt() + "'" +
            ", resultReleaseMode='" + getResultReleaseMode() + "'" +
            ", answerReleaseMode='" + getAnswerReleaseMode() + "'" +
            ", version=" + getVersion() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", assessmentType='" + getAssessmentType() + "'" +
            ", videoTriggerSeconds=" + getVideoTriggerSeconds() +
            ", assessmentConfig='" + getAssessmentConfig() + "'" +
            ", securityConfig='" + getSecurityConfig() + "'" +
            "}";
    }
}
