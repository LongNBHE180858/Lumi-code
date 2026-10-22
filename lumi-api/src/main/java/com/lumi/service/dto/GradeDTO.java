package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.Grade} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GradeDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    @Max(value = 12)
    private Integer level;

    @NotNull
    @Size(max = 50)
    private String name;

    private Boolean active;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GradeDTO)) {
            return false;
        }

        GradeDTO gradeDTO = (GradeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, gradeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GradeDTO{" +
            "id=" + getId() +
            ", level=" + getLevel() +
            ", name='" + getName() + "'" +
            "}";
    }
}
