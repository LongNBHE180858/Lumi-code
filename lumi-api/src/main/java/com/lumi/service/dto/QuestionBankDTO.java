package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.QuestionBank} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuestionBankDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    private UserDTO instructor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserDTO getInstructor() {
        return instructor;
    }

    public void setInstructor(UserDTO instructor) {
        this.instructor = instructor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuestionBankDTO)) {
            return false;
        }

        QuestionBankDTO questionBankDTO = (QuestionBankDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, questionBankDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuestionBankDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", instructor=" + getInstructor() +
            "}";
    }
}
