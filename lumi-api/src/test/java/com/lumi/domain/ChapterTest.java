package com.lumi.domain;

import static com.lumi.domain.ChapterTestSamples.*;
import static com.lumi.domain.CourseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChapterTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Chapter.class);
        Chapter chapter1 = getChapterSample1();
        Chapter chapter2 = new Chapter();
        assertThat(chapter1).isNotEqualTo(chapter2);

        chapter2.setId(chapter1.getId());
        assertThat(chapter1).isEqualTo(chapter2);

        chapter2 = getChapterSample2();
        assertThat(chapter1).isNotEqualTo(chapter2);
    }

    @Test
    void courseTest() {
        Chapter chapter = getChapterRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        chapter.setCourse(courseBack);
        assertThat(chapter.getCourse()).isEqualTo(courseBack);

        chapter.course(null);
        assertThat(chapter.getCourse()).isNull();
    }
}
