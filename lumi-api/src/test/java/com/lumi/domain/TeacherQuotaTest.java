package com.lumi.domain;

import static com.lumi.domain.SystemPlanTestSamples.*;
import static com.lumi.domain.TeacherQuotaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TeacherQuotaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeacherQuota.class);
        TeacherQuota teacherQuota1 = getTeacherQuotaSample1();
        TeacherQuota teacherQuota2 = new TeacherQuota();
        assertThat(teacherQuota1).isNotEqualTo(teacherQuota2);

        teacherQuota2.setId(teacherQuota1.getId());
        assertThat(teacherQuota1).isEqualTo(teacherQuota2);

        teacherQuota2 = getTeacherQuotaSample2();
        assertThat(teacherQuota1).isNotEqualTo(teacherQuota2);
    }

    @Test
    void currentPlanTest() {
        TeacherQuota teacherQuota = getTeacherQuotaRandomSampleGenerator();
        SystemPlan systemPlanBack = getSystemPlanRandomSampleGenerator();

        teacherQuota.setCurrentPlan(systemPlanBack);
        assertThat(teacherQuota.getCurrentPlan()).isEqualTo(systemPlanBack);

        teacherQuota.currentPlan(null);
        assertThat(teacherQuota.getCurrentPlan()).isNull();
    }
}
