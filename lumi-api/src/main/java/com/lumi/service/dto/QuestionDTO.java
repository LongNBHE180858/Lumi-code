package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.Question} entity.
 */
@Schema(description = "Lưu câu hỏi, nguồn tạo, phân loại và liên kết phiên bản trước.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuestionDTO implements Serializable {

    private Long id;

    @Lob
    private String content;

    @NotNull
    @Size(max = 20)
    private String type;

    @Min(value = 1)
    @Max(value = 5)
    private Integer difficulty;

    @Lob
    private String explanation;

    private Boolean isDeleted;

    @NotNull
    @Size(max = 20)
    private String sourceType;

    @Size(max = 64)
    private String contentHash;

    @NotNull
    @Size(max = 20)
    private String visibility;

    @NotNull
    private Long version;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    private QuestionBankDTO bank;

    @NotNull
    private UserDTO createdBy;

    private AiGeneratedContentDTO aiContent;

    private SubjectDTO subject;

    private GradeDTO grade;

    private QuestionDTO previousQuestion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public QuestionBankDTO getBank() {
        return bank;
    }

    public void setBank(QuestionBankDTO bank) {
        this.bank = bank;
    }

    public UserDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserDTO createdBy) {
        this.createdBy = createdBy;
    }

    public AiGeneratedContentDTO getAiContent() {
        return aiContent;
    }

    public void setAiContent(AiGeneratedContentDTO aiContent) {
        this.aiContent = aiContent;
    }

    public SubjectDTO getSubject() {
        return subject;
    }

    public void setSubject(SubjectDTO subject) {
        this.subject = subject;
    }

    public GradeDTO getGrade() {
        return grade;
    }

    public void setGrade(GradeDTO grade) {
        this.grade = grade;
    }

    public QuestionDTO getPreviousQuestion() {
        return previousQuestion;
    }

    public void setPreviousQuestion(QuestionDTO previousQuestion) {
        this.previousQuestion = previousQuestion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuestionDTO)) {
            return false;
        }

        QuestionDTO questionDTO = (QuestionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, questionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuestionDTO{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", type='" + getType() + "'" +
            ", difficulty=" + getDifficulty() +
            ", explanation='" + getExplanation() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", sourceType='" + getSourceType() + "'" +
            ", contentHash='" + getContentHash() + "'" +
            ", visibility='" + getVisibility() + "'" +
            ", version=" + getVersion() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", bank=" + getBank() +
            ", createdBy=" + getCreatedBy() +
            ", aiContent=" + getAiContent() +
            ", subject=" + getSubject() +
            ", grade=" + getGrade() +
            ", previousQuestion=" + getPreviousQuestion() +
            "}";
    }
}
