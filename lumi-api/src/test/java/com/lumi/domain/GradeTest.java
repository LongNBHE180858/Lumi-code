package com.lumi.domain;

import static com.lumi.domain.GradeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GradeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Grade.class);
        Grade grade1 = getGradeSample1();
        Grade grade2 = new Grade();
        assertThat(grade1).isNotEqualTo(grade2);

        grade2.setId(grade1.getId());
        assertThat(grade1).isEqualTo(grade2);

        grade2 = getGradeSample2();
        assertThat(grade1).isNotEqualTo(grade2);
    }
}
