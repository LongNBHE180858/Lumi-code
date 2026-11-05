package com.lumi.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO representing a node in the Knowledge Graph for a Subject.
 */
public class KnowledgeGraphNodeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank
    @Size(max = 255)
    private String title;

    @Size(max = 50)
    private String type;

    private Long subjectId;

    public KnowledgeGraphNodeDTO() {}

    public KnowledgeGraphNodeDTO(Long id, String title, String type, Long subjectId) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.subjectId = subjectId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KnowledgeGraphNodeDTO that = (KnowledgeGraphNodeDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }

    @Override
    public String toString() {
        return "KnowledgeGraphNodeDTO{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", type='" + type + '\'' +
            ", subjectId=" + subjectId +
            '}';
    }
}
