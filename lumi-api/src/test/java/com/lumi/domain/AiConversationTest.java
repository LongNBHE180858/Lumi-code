package com.lumi.domain;

import static com.lumi.domain.AiConversationTestSamples.*;
import static com.lumi.domain.LessonTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AiConversationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AiConversation.class);
        AiConversation aiConversation1 = getAiConversationSample1();
        AiConversation aiConversation2 = new AiConversation();
        assertThat(aiConversation1).isNotEqualTo(aiConversation2);

        aiConversation2.setId(aiConversation1.getId());
        assertThat(aiConversation1).isEqualTo(aiConversation2);

        aiConversation2 = getAiConversationSample2();
        assertThat(aiConversation1).isNotEqualTo(aiConversation2);
    }

    @Test
    void lessonTest() {
        AiConversation aiConversation = getAiConversationRandomSampleGenerator();
        Lesson lessonBack = getLessonRandomSampleGenerator();

        aiConversation.setLesson(lessonBack);
        assertThat(aiConversation.getLesson()).isEqualTo(lessonBack);

        aiConversation.lesson(null);
        assertThat(aiConversation.getLesson()).isNull();
    }
}
