package com.lumi.domain;

import static com.lumi.domain.AiGeneratedContentTestSamples.*;
import static com.lumi.domain.CourseTestSamples.*;
import static com.lumi.domain.LearningDocumentTestSamples.*;
import static com.lumi.domain.LessonTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AiGeneratedContentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AiGeneratedContent.class);
        AiGeneratedContent aiGeneratedContent1 = getAiGeneratedContentSample1();
        AiGeneratedContent aiGeneratedContent2 = new AiGeneratedContent();
        assertThat(aiGeneratedContent1).isNotEqualTo(aiGeneratedContent2);

        aiGeneratedContent2.setId(aiGeneratedContent1.getId());
        assertThat(aiGeneratedContent1).isEqualTo(aiGeneratedContent2);

        aiGeneratedContent2 = getAiGeneratedContentSample2();
        assertThat(aiGeneratedContent1).isNotEqualTo(aiGeneratedContent2);
    }

    @Test
    void documentTest() {
        AiGeneratedContent aiGeneratedContent = getAiGeneratedContentRandomSampleGenerator();
        LearningDocument learningDocumentBack = getLearningDocumentRandomSampleGenerator();

        aiGeneratedContent.setDocument(learningDocumentBack);
        assertThat(aiGeneratedContent.getDocument()).isEqualTo(learningDocumentBack);

        aiGeneratedContent.document(null);
        assertThat(aiGeneratedContent.getDocument()).isNull();
    }

    @Test
    void lessonTest() {
        AiGeneratedContent aiGeneratedContent = getAiGeneratedContentRandomSampleGenerator();
        Lesson lessonBack = getLessonRandomSampleGenerator();

        aiGeneratedContent.setLesson(lessonBack);
        assertThat(aiGeneratedContent.getLesson()).isEqualTo(lessonBack);

        aiGeneratedContent.lesson(null);
        assertThat(aiGeneratedContent.getLesson()).isNull();
    }

    @Test
    void courseTest() {
        AiGeneratedContent aiGeneratedContent = getAiGeneratedContentRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        aiGeneratedContent.setCourse(courseBack);
        assertThat(aiGeneratedContent.getCourse()).isEqualTo(courseBack);

        aiGeneratedContent.course(null);
        assertThat(aiGeneratedContent.getCourse()).isNull();
    }
}
