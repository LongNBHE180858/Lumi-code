package com.lumi.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class QuestionKnowledgeLinkTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static QuestionKnowledgeLink getQuestionKnowledgeLinkSample1() {
        return new QuestionKnowledgeLink().id(1L);
    }

    public static QuestionKnowledgeLink getQuestionKnowledgeLinkSample2() {
        return new QuestionKnowledgeLink().id(2L);
    }

    public static QuestionKnowledgeLink getQuestionKnowledgeLinkRandomSampleGenerator() {
        return new QuestionKnowledgeLink().id(longCount.incrementAndGet());
    }
}
