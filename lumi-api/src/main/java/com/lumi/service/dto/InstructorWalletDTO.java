package com.lumi.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.InstructorWallet} entity.
 */
@Schema(description = "Lưu số dư tiền chờ giải phóng và tiền khả dụng của giáo viên.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InstructorWalletDTO implements Serializable {

    private Long id;

    private BigDecimal pendingAmount;

    private BigDecimal availableAmount;

    private Long rowVersion;

    @NotNull
    private UserDTO instructor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPendingAmount() {
        return pendingAmount;
    }

    public void setPendingAmount(BigDecimal pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    public Long getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(Long rowVersion) {
        this.rowVersion = rowVersion;
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
        if (!(o instanceof InstructorWalletDTO)) {
            return false;
        }

        InstructorWalletDTO instructorWalletDTO = (InstructorWalletDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, instructorWalletDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InstructorWalletDTO{" +
            "id=" + getId() +
            ", pendingAmount=" + getPendingAmount() +
            ", availableAmount=" + getAvailableAmount() +
            ", rowVersion=" + getRowVersion() +
            ", instructor=" + getInstructor() +
            "}";
    }
}
