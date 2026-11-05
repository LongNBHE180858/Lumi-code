package com.lumi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Lưu câu hỏi, nguồn tạo, phân loại và liên kết phiên bản trước.
 */
@Entity
@Table(name = "questions")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Question implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @NotNull
    @Size(max = 20)
    @Column(name = "type", length = 20, nullable = false)
    private String type;

    @Min(value = 1)
    @Max(value = 5)
    @Column(name = "difficulty")
    private Integer difficulty;

    @Column(name = "explanation", columnDefinition = "text")
    private String explanation;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @NotNull
    @Size(max = 20)
    @Column(name = "source_type", length = 20, nullable = false)
    private String sourceType;

    @Size(max = 64)
    @Column(name = "content_hash", length = 64)
    private String contentHash;

    @NotNull
    @Size(max = 20)
    @Column(name = "visibility", length = 20, nullable = false)
    private String visibility;

    @NotNull
    @Column(name = "version", nullable = false)
    private Long version;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "instructor", "subject", "grade" }, allowSetters = true)
    private QuestionBank bank;

    @ManyToOne(optional = false)
    @NotNull
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "createdBy", "document", "lesson", "reviewedBy", "course" }, allowSetters = true)
    private AiGeneratedContent aiContent;

    @ManyToOne(fetch = FetchType.LAZY)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    private Grade grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "bank", "createdBy", "aiContent", "subject", "grade", "previousQuestion" }, allowSetters = true)
    private Question previousQuestion;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Question id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public Question content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return this.type;
    }

    public Question type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDifficulty() {
        return this.difficulty;
    }

    public Question difficulty(Integer difficulty) {
        this.setDifficulty(difficulty);
        return this;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public String getExplanation() {
        return this.explanation;
    }

    public Question explanation(String explanation) {
        this.setExplanation(explanation);
        return this;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public Question isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getSourceType() {
        return this.sourceType;
    }

    public Question sourceType(String sourceType) {
        this.setSourceType(sourceType);
        return this;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getContentHash() {
        return this.contentHash;
    }

    public Question contentHash(String contentHash) {
        this.setContentHash(contentHash);
        return this;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public String getVisibility() {
        return this.visibility;
    }

    public Question visibility(String visibility) {
        this.setVisibility(visibility);
        return this;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Long getVersion() {
        return this.version;
    }

    public Question version(Long version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Question createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Question updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public QuestionBank getBank() {
        return this.bank;
    }

    public void setBank(QuestionBank questionBank) {
        this.bank = questionBank;
    }

    public Question bank(QuestionBank questionBank) {
        this.setBank(questionBank);
        return this;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public Question createdBy(User user) {
        this.setCreatedBy(user);
        return this;
    }

    public AiGeneratedContent getAiContent() {
        return this.aiContent;
    }

    public void setAiContent(AiGeneratedContent aiGeneratedContent) {
        this.aiContent = aiGeneratedContent;
    }

    public Question aiContent(AiGeneratedContent aiGeneratedContent) {
        this.setAiContent(aiGeneratedContent);
        return this;
    }

    public Subject getSubject() {
        return this.subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Question subject(Subject subject) {
        this.setSubject(subject);
        return this;
    }

    public Grade getGrade() {
        return this.grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public Question grade(Grade grade) {
        this.setGrade(grade);
        return this;
    }

    public Question getPreviousQuestion() {
        return this.previousQuestion;
    }

    public void setPreviousQuestion(Question question) {
        this.previousQuestion = question;
    }

    public Question previousQuestion(Question question) {
        this.setPreviousQuestion(question);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Question)) {
            return false;
        }
        return getId() != null && getId().equals(((Question) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Question{" +
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
            "}";
    }
}
