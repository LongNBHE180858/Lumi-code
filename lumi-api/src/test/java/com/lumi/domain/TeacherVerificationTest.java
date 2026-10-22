package com.lumi.domain;

import static com.lumi.domain.TeacherVerificationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TeacherVerificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeacherVerification.class);
        TeacherVerification teacherVerification1 = getTeacherVerificationSample1();
        TeacherVerification teacherVerification2 = new TeacherVerification();
        assertThat(teacherVerification1).isNotEqualTo(teacherVerification2);

        teacherVerification2.setId(teacherVerification1.getId());
        assertThat(teacherVerification1).isEqualTo(teacherVerification2);

        teacherVerification2 = getTeacherVerificationSample2();
        assertThat(teacherVerification1).isNotEqualTo(teacherVerification2);
    }
}
