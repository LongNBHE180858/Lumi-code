package com.lumi.domain;

import static com.lumi.domain.CourseMaterialTestSamples.*;
import static com.lumi.domain.LessonTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CourseMaterialTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseMaterial.class);
        CourseMaterial courseMaterial1 = getCourseMaterialSample1();
        CourseMaterial courseMaterial2 = new CourseMaterial();
        assertThat(courseMaterial1).isNotEqualTo(courseMaterial2);

        courseMaterial2.setId(courseMaterial1.getId());
        assertThat(courseMaterial1).isEqualTo(courseMaterial2);

        courseMaterial2 = getCourseMaterialSample2();
        assertThat(courseMaterial1).isNotEqualTo(courseMaterial2);
    }

    @Test
    void lessonTest() {
        CourseMaterial courseMaterial = getCourseMaterialRandomSampleGenerator();
        Lesson lessonBack = getLessonRandomSampleGenerator();

        courseMaterial.setLesson(lessonBack);
        assertThat(courseMaterial.getLesson()).isEqualTo(lessonBack);

        courseMaterial.lesson(null);
        assertThat(courseMaterial.getLesson()).isNull();
    }
}
