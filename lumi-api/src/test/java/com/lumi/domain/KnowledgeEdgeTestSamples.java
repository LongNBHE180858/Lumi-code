package com.lumi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class KnowledgeEdgeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static KnowledgeEdge getKnowledgeEdgeSample1() {
        return new KnowledgeEdge().id(1L).relationType("relationType1");
    }

    public static KnowledgeEdge getKnowledgeEdgeSample2() {
        return new KnowledgeEdge().id(2L).relationType("relationType2");
    }

    public static KnowledgeEdge getKnowledgeEdgeRandomSampleGenerator() {
        return new KnowledgeEdge().id(longCount.incrementAndGet()).relationType(UUID.randomUUID().toString());
    }
}
