package com.lumi.domain;

import static com.lumi.domain.SystemPlanTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SystemPlanTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SystemPlan.class);
        SystemPlan systemPlan1 = getSystemPlanSample1();
        SystemPlan systemPlan2 = new SystemPlan();
        assertThat(systemPlan1).isNotEqualTo(systemPlan2);

        systemPlan2.setId(systemPlan1.getId());
        assertThat(systemPlan1).isEqualTo(systemPlan2);

        systemPlan2 = getSystemPlanSample2();
        assertThat(systemPlan1).isNotEqualTo(systemPlan2);
    }
}
