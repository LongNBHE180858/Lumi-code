package com.lumi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Lưu chuỗi ngày học và số dư điểm thưởng tổng hợp của học sinh.
 */
@Entity
@Table(name = "student_streaks")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StudentStreak implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "current_streak")
    private Integer currentStreak;

    @Column(name = "longest_streak")
    private Integer longestStreak;

    @Column(name = "gems_balance")
    private Integer gemsBalance;

    @Column(name = "last_active_date")
    private LocalDate lastActiveDate;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private User student;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StudentStreak id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCurrentStreak() {
        return this.currentStreak;
    }

    public StudentStreak currentStreak(Integer currentStreak) {
        this.setCurrentStreak(currentStreak);
        return this;
    }

    public void setCurrentStreak(Integer currentStreak) {
        this.currentStreak = currentStreak;
    }

    public Integer getLongestStreak() {
        return this.longestStreak;
    }

    public StudentStreak longestStreak(Integer longestStreak) {
        this.setLongestStreak(longestStreak);
        return this;
    }

    public void setLongestStreak(Integer longestStreak) {
        this.longestStreak = longestStreak;
    }

    public Integer getGemsBalance() {
        return this.gemsBalance;
    }

    public StudentStreak gemsBalance(Integer gemsBalance) {
        this.setGemsBalance(gemsBalance);
        return this;
    }

    public void setGemsBalance(Integer gemsBalance) {
        this.gemsBalance = gemsBalance;
    }

    public LocalDate getLastActiveDate() {
        return this.lastActiveDate;
    }

    public StudentStreak lastActiveDate(LocalDate lastActiveDate) {
        this.setLastActiveDate(lastActiveDate);
        return this;
    }

    public void setLastActiveDate(LocalDate lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    public User getStudent() {
        return this.student;
    }

    public void setStudent(User user) {
        this.student = user;
    }

    public StudentStreak student(User user) {
        this.setStudent(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StudentStreak)) {
            return false;
        }
        return getId() != null && getId().equals(((StudentStreak) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StudentStreak{" +
            "id=" + getId() +
            ", currentStreak=" + getCurrentStreak() +
            ", longestStreak=" + getLongestStreak() +
            ", gemsBalance=" + getGemsBalance() +
            ", lastActiveDate='" + getLastActiveDate() + "'" +
            "}";
    }
}
