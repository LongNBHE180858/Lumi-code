package com.lumi.domain;

import static com.lumi.domain.ChapterTestSamples.*;
import static com.lumi.domain.LessonTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LessonTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Lesson.class);
        Lesson lesson1 = getLessonSample1();
        Lesson lesson2 = new Lesson();
        assertThat(lesson1).isNotEqualTo(lesson2);

        lesson2.setId(lesson1.getId());
        assertThat(lesson1).isEqualTo(lesson2);

        lesson2 = getLessonSample2();
        assertThat(lesson1).isNotEqualTo(lesson2);
    }

    @Test
    void chapterTest() {
        Lesson lesson = getLessonRandomSampleGenerator();
        Chapter chapterBack = getChapterRandomSampleGenerator();

        lesson.setChapter(chapterBack);
        assertThat(lesson.getChapter()).isEqualTo(chapterBack);

        lesson.chapter(null);
        assertThat(lesson.getChapter()).isNull();
    }
}
