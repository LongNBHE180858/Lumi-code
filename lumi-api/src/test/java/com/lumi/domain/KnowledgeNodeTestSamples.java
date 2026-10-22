package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class KnowledgeNodeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static KnowledgeNode getKnowledgeNodeSample1() {
        return new KnowledgeNode().id(1L).title("title1").type("type1");
    }

    public static KnowledgeNode getKnowledgeNodeSample2() {
        return new KnowledgeNode().id(2L).title("title2").type("type2");
    }

    public static KnowledgeNode getKnowledgeNodeRandomSampleGenerator() {
        return new KnowledgeNode().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString()).type(UUID.randomUUID().toString());
    }
}
