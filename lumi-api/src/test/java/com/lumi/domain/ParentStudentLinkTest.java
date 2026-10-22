package com.lumi.domain;

import static com.lumi.domain.ParentStudentLinkTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ParentStudentLinkTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParentStudentLink.class);
        ParentStudentLink parentStudentLink1 = getParentStudentLinkSample1();
        ParentStudentLink parentStudentLink2 = new ParentStudentLink();
        assertThat(parentStudentLink1).isNotEqualTo(parentStudentLink2);

        parentStudentLink2.setId(parentStudentLink1.getId());
        assertThat(parentStudentLink1).isEqualTo(parentStudentLink2);

        parentStudentLink2 = getParentStudentLinkSample2();
        assertThat(parentStudentLink1).isNotEqualTo(parentStudentLink2);
    }
}
