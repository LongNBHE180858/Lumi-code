package com.lumi.service.ai.demo;

import com.lumi.service.ai.RagChatService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = { "lumi.ai.demo-retrieval", "lumi.ai.chat.enabled" },
    havingValue = "true"
)
public class DemoRetrievalRunner implements ApplicationRunner {

    private static final Logger LOG =
        LoggerFactory.getLogger(DemoRetrievalRunner.class);

    private final RagChatService ragChatService;

    @Value("${lumi.ai.demo-owner-login:}")
    private String ownerLogin;

    @Value("${lumi.ai.demo-question:Thủ đô nước pháp là ?}")
    private String question;

    @Override
    public void run(ApplicationArguments args) {
        var result = ragChatService.chat(ownerLogin, question);

        LOG.info("DEMO QUESTION: {}", question);
        LOG.info("DEMO STATUS: {}", result.status());
        LOG.info("DEMO ANSWER: {}", result.answer());
        LOG.info("DEMO SOURCES: {}", result.citations());
    }
}
