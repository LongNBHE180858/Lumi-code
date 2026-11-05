package com.lumi.domain;

import static com.lumi.domain.CourseEnrollmentTestSamples.*;
import static com.lumi.domain.LessonProgressTestSamples.*;
import static com.lumi.domain.LessonTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LessonProgressTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LessonProgress.class);
        LessonProgress lessonProgress1 = getLessonProgressSample1();
        LessonProgress lessonProgress2 = new LessonProgress();
        assertThat(lessonProgress1).isNotEqualTo(lessonProgress2);

        lessonProgress2.setId(lessonProgress1.getId());
        assertThat(lessonProgress1).isEqualTo(lessonProgress2);

        lessonProgress2 = getLessonProgressSample2();
        assertThat(lessonProgress1).isNotEqualTo(lessonProgress2);
    }

    @Test
    void enrollmentTest() {
        LessonProgress lessonProgress = getLessonProgressRandomSampleGenerator();
        CourseEnrollment courseEnrollmentBack = getCourseEnrollmentRandomSampleGenerator();

        lessonProgress.setEnrollment(courseEnrollmentBack);
        assertThat(lessonProgress.getEnrollment()).isEqualTo(courseEnrollmentBack);

        lessonProgress.enrollment(null);
        assertThat(lessonProgress.getEnrollment()).isNull();
    }

    @Test
    void lessonTest() {
        LessonProgress lessonProgress = getLessonProgressRandomSampleGenerator();
        Lesson lessonBack = getLessonRandomSampleGenerator();

        lessonProgress.setLesson(lessonBack);
        assertThat(lessonProgress.getLesson()).isEqualTo(lessonBack);

        lessonProgress.lesson(null);
        assertThat(lessonProgress.getLesson()).isNull();
    }
}
