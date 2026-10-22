package com.lumi.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumi.domain.StudentStreak} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StudentStreakDTO implements Serializable {

    private Long id;

    @Min(value = 0)
    private Integer currentStreak;

    @Min(value = 0)
    private Integer longestStreak;

    @Min(value = 0)
    private Integer gemsBalance;

    private LocalDate lastActiveDate;

    @NotNull
    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(Integer currentStreak) {
        this.currentStreak = currentStreak;
    }

    public Integer getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(Integer longestStreak) {
        this.longestStreak = longestStreak;
    }

    public Integer getGemsBalance() {
        return gemsBalance;
    }

    public void setGemsBalance(Integer gemsBalance) {
        this.gemsBalance = gemsBalance;
    }

    public LocalDate getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(LocalDate lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StudentStreakDTO)) {
            return false;
        }

        StudentStreakDTO studentStreakDTO = (StudentStreakDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, studentStreakDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StudentStreakDTO{" +
            "id=" + getId() +
            ", currentStreak=" + getCurrentStreak() +
            ", longestStreak=" + getLongestStreak() +
            ", gemsBalance=" + getGemsBalance() +
            ", lastActiveDate='" + getLastActiveDate() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
