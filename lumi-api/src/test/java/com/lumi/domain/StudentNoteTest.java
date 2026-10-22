package com.lumi.domain;

import static com.lumi.domain.LessonTestSamples.*;
import static com.lumi.domain.StudentNoteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StudentNoteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StudentNote.class);
        StudentNote studentNote1 = getStudentNoteSample1();
        StudentNote studentNote2 = new StudentNote();
        assertThat(studentNote1).isNotEqualTo(studentNote2);

        studentNote2.setId(studentNote1.getId());
        assertThat(studentNote1).isEqualTo(studentNote2);

        studentNote2 = getStudentNoteSample2();
        assertThat(studentNote1).isNotEqualTo(studentNote2);
    }

    @Test
    void lessonTest() {
        StudentNote studentNote = getStudentNoteRandomSampleGenerator();
        Lesson lessonBack = getLessonRandomSampleGenerator();

        studentNote.setLesson(lessonBack);
        assertThat(studentNote.getLesson()).isEqualTo(lessonBack);

        studentNote.lesson(null);
        assertThat(studentNote.getLesson()).isNull();
    }
}
