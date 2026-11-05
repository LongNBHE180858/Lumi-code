package com.lumi.web.rest;

import com.lumi.domain.Question;
import com.lumi.domain.User;
import jakarta.persistence.EntityManager;
import java.time.Instant;

/** Test data shared by the remaining resource integration tests. */
final class QuestionTestFixtures {

    private QuestionTestFixtures() {}

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";

    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";

    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final Integer DEFAULT_DIFFICULTY = 1;

    private static final Integer UPDATED_DIFFICULTY = 2;

    private static final String DEFAULT_EXPLANATION = "AAAAAAAAAA";

    private static final String UPDATED_EXPLANATION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_DELETED = false;

    private static final Boolean UPDATED_IS_DELETED = true;

    private static final String DEFAULT_SOURCE_TYPE = "AAAAAAAAAA";

    private static final String UPDATED_SOURCE_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT_HASH = "AAAAAAAAAA";

    private static final String UPDATED_CONTENT_HASH = "BBBBBBBBBB";

    private static final String DEFAULT_VISIBILITY = "AAAAAAAAAA";

    private static final String UPDATED_VISIBILITY = "BBBBBBBBBB";

    private static final Long DEFAULT_VERSION = 1L;

    private static final Long UPDATED_VERSION = 2L;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);

    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1791835009362L);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);

    private static final Instant UPDATED_UPDATED_AT = Instant.ofEpochMilli(1791835009362L);

    public static Question createEntity(EntityManager em) {
        Question question = new Question()
            .content(DEFAULT_CONTENT)
            .type(DEFAULT_TYPE)
            .difficulty(DEFAULT_DIFFICULTY)
            .explanation(DEFAULT_EXPLANATION)
            .isDeleted(DEFAULT_IS_DELETED)
            .sourceType(DEFAULT_SOURCE_TYPE)
            .contentHash(DEFAULT_CONTENT_HASH)
            .visibility(DEFAULT_VISIBILITY)
            .version(DEFAULT_VERSION)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        question.setCreatedBy(user);
        return question;
    }

    public static Question createUpdatedEntity(EntityManager em) {
        Question updatedQuestion = new Question()
            .content(UPDATED_CONTENT)
            .type(UPDATED_TYPE)
            .difficulty(UPDATED_DIFFICULTY)
            .explanation(UPDATED_EXPLANATION)
            .isDeleted(UPDATED_IS_DELETED)
            .sourceType(UPDATED_SOURCE_TYPE)
            .contentHash(UPDATED_CONTENT_HASH)
            .visibility(UPDATED_VISIBILITY)
            .version(UPDATED_VERSION)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedQuestion.setCreatedBy(user);
        return updatedQuestion;
    }
}
