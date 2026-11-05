package com.lumi.domain;

import static com.lumi.domain.DiscussionTestSamples.*;
import static com.lumi.domain.LessonTestSamples.*;
import static com.lumi.domain.StudyClassTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DiscussionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Discussion.class);
        Discussion discussion1 = getDiscussionSample1();
        Discussion discussion2 = new Discussion();
        assertThat(discussion1).isNotEqualTo(discussion2);

        discussion2.setId(discussion1.getId());
        assertThat(discussion1).isEqualTo(discussion2);

        discussion2 = getDiscussionSample2();
        assertThat(discussion1).isNotEqualTo(discussion2);
    }

    @Test
    void lessonTest() {
        Discussion discussion = getDiscussionRandomSampleGenerator();
        Lesson lessonBack = getLessonRandomSampleGenerator();

        discussion.setLesson(lessonBack);
        assertThat(discussion.getLesson()).isEqualTo(lessonBack);

        discussion.lesson(null);
        assertThat(discussion.getLesson()).isNull();
    }

    @Test
    void studyClassTest() {
        Discussion discussion = getDiscussionRandomSampleGenerator();
        StudyClass studyClassBack = getStudyClassRandomSampleGenerator();

        discussion.setStudyClass(studyClassBack);
        assertThat(discussion.getStudyClass()).isEqualTo(studyClassBack);

        discussion.studyClass(null);
        assertThat(discussion.getStudyClass()).isNull();
    }
}
