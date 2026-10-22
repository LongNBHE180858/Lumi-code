package com.lumi.domain;

import static com.lumi.domain.StudentStreakTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StudentStreakTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StudentStreak.class);
        StudentStreak studentStreak1 = getStudentStreakSample1();
        StudentStreak studentStreak2 = new StudentStreak();
        assertThat(studentStreak1).isNotEqualTo(studentStreak2);

        studentStreak2.setId(studentStreak1.getId());
        assertThat(studentStreak1).isEqualTo(studentStreak2);

        studentStreak2 = getStudentStreakSample2();
        assertThat(studentStreak1).isNotEqualTo(studentStreak2);
    }
}
