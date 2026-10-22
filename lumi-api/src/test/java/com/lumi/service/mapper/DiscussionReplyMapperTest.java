package com.lumi.service.mapper;

import static com.lumi.domain.DiscussionReplyAsserts.*;
import static com.lumi.domain.DiscussionReplyTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DiscussionReplyMapperTest {

    private DiscussionReplyMapper discussionReplyMapper;

    @BeforeEach
    void setUp() {
        discussionReplyMapper = new DiscussionReplyMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDiscussionReplySample1();
        var actual = discussionReplyMapper.toEntity(discussionReplyMapper.toDto(expected));
        assertDiscussionReplyAllPropertiesEquals(expected, actual);
    }
}
