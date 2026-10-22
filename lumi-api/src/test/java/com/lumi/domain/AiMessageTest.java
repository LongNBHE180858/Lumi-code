package com.lumi.domain;

import static com.lumi.domain.AiConversationTestSamples.*;
import static com.lumi.domain.AiMessageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AiMessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AiMessage.class);
        AiMessage aiMessage1 = getAiMessageSample1();
        AiMessage aiMessage2 = new AiMessage();
        assertThat(aiMessage1).isNotEqualTo(aiMessage2);

        aiMessage2.setId(aiMessage1.getId());
        assertThat(aiMessage1).isEqualTo(aiMessage2);

        aiMessage2 = getAiMessageSample2();
        assertThat(aiMessage1).isNotEqualTo(aiMessage2);
    }

    @Test
    void conversationTest() {
        AiMessage aiMessage = getAiMessageRandomSampleGenerator();
        AiConversation aiConversationBack = getAiConversationRandomSampleGenerator();

        aiMessage.setConversation(aiConversationBack);
        assertThat(aiMessage.getConversation()).isEqualTo(aiConversationBack);

        aiMessage.conversation(null);
        assertThat(aiMessage.getConversation()).isNull();
    }
}
