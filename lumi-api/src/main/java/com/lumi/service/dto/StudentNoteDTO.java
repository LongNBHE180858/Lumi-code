package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.StudentNote} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StudentNoteDTO implements Serializable {

    private Long id;

    @Min(value = 0)
    private Integer videoTimestamp;

    @NotNull
    @Size(max = 5000)
    private String content;

    private Instant createdAt;

    @NotNull
    private UserDTO student;

    @NotNull
    private LessonDTO lesson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVideoTimestamp() {
        return videoTimestamp;
    }

    public void setVideoTimestamp(Integer videoTimestamp) {
        this.videoTimestamp = videoTimestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UserDTO getStudent() {
        return student;
    }

    public void setStudent(UserDTO student) {
        this.student = student;
    }

    public LessonDTO getLesson() {
        return lesson;
    }

    public void setLesson(LessonDTO lesson) {
        this.lesson = lesson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StudentNoteDTO)) {
            return false;
        }

        StudentNoteDTO studentNoteDTO = (StudentNoteDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, studentNoteDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StudentNoteDTO{" +
            "id=" + getId() +
            ", videoTimestamp=" + getVideoTimestamp() +
            ", content='" + getContent() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", student=" + getStudent() +
            ", lesson=" + getLesson() +
            "}";
    }
}
