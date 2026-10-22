package com.lumi.service.dto;

import com.lumi.domain.enumeration.FeedbackType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * A DTO for creating a new Feedback.
 */
public class FeedbackCreateDTO implements Serializable {

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotNull
    private FeedbackType type;

    @NotBlank
    @Size(min = 10, max = 5000)
    private String content;

    @NotBlank
    @Email
    @Size(max = 254)
    private String contactEmail;

    private Long courseId;

    private Long lessonId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public FeedbackType getType() {
        return type;
    }

    public void setType(FeedbackType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    @Override
    public String toString() {
        return "FeedbackCreateDTO{" +
            "title='" + title + '\'' +
            ", type=" + type +
            ", content='" + content + '\'' +
            ", contactEmail='" + contactEmail + '\'' +
            ", courseId=" + courseId +
            ", lessonId=" + lessonId +
            '}';
    }
}
