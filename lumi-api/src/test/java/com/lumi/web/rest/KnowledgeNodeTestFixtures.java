package com.lumi.web.rest;

import com.lumi.domain.KnowledgeNode;

/** Test data shared by the remaining resource integration tests. */
final class KnowledgeNodeTestFixtures {

    private KnowledgeNodeTestFixtures() {}

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";

    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";

    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    public static KnowledgeNode createEntity() {
        return new KnowledgeNode().title(DEFAULT_TITLE).type(DEFAULT_TYPE);
    }

    public static KnowledgeNode createUpdatedEntity() {
        return new KnowledgeNode().title(UPDATED_TITLE).type(UPDATED_TYPE);
    }
}
