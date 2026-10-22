package com.lumi.domain;

import static com.lumi.domain.CategoryTestSamples.*;
import static com.lumi.domain.CourseTestSamples.*;
import static com.lumi.domain.GradeTestSamples.*;
import static com.lumi.domain.SubjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CourseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Course.class);
        Course course1 = getCourseSample1();
        Course course2 = new Course();
        assertThat(course1).isNotEqualTo(course2);

        course2.setId(course1.getId());
        assertThat(course1).isEqualTo(course2);

        course2 = getCourseSample2();
        assertThat(course1).isNotEqualTo(course2);
    }

    @Test
    void categoryTest() {
        Course course = getCourseRandomSampleGenerator();
        Category categoryBack = getCategoryRandomSampleGenerator();

        course.setCategory(categoryBack);
        assertThat(course.getCategory()).isEqualTo(categoryBack);

        course.category(null);
        assertThat(course.getCategory()).isNull();
    }

    @Test
    void subjectTest() {
        Course course = getCourseRandomSampleGenerator();
        Subject subjectBack = getSubjectRandomSampleGenerator();

        course.setSubject(subjectBack);
        assertThat(course.getSubject()).isEqualTo(subjectBack);

        course.subject(null);
        assertThat(course.getSubject()).isNull();
    }

    @Test
    void gradeTest() {
        Course course = getCourseRandomSampleGenerator();
        Grade gradeBack = getGradeRandomSampleGenerator();

        course.setGrade(gradeBack);
        assertThat(course.getGrade()).isEqualTo(gradeBack);

        course.grade(null);
        assertThat(course.getGrade()).isNull();
    }
}
