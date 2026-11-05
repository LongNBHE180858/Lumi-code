package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.KnowledgeNode} entity.
 */
@Schema(description = "Lưu các đơn vị kiến thức dùng để phân tích năng lực và gợi ý học tập.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class KnowledgeNodeDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String title;

    @Size(max = 50)
    private String type;

    private SubjectDTO subject;

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

    public SubjectDTO getSubject() {
        return subject;
    }

    public void setSubject(SubjectDTO subject) {
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KnowledgeNodeDTO)) {
            return false;
        }

        KnowledgeNodeDTO knowledgeNodeDTO = (KnowledgeNodeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, knowledgeNodeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KnowledgeNodeDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", type='" + getType() + "'" +
            ", subject=" + getSubject() +
            "}";
    }
}
