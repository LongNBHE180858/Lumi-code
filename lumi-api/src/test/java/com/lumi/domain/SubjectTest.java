package com.lumi.domain;

import static com.lumi.domain.SubjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SubjectTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Subject.class);
        Subject subject1 = getSubjectSample1();
        Subject subject2 = new Subject();
        assertThat(subject1).isNotEqualTo(subject2);

        subject2.setId(subject1.getId());
        assertThat(subject1).isEqualTo(subject2);

        subject2 = getSubjectSample2();
        assertThat(subject1).isNotEqualTo(subject2);
    }
}
