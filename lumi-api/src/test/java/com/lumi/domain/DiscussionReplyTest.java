package com.lumi.domain;

import static com.lumi.domain.DiscussionReplyTestSamples.*;
import static com.lumi.domain.DiscussionReplyTestSamples.*;
import static com.lumi.domain.DiscussionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DiscussionReplyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DiscussionReply.class);
        DiscussionReply discussionReply1 = getDiscussionReplySample1();
        DiscussionReply discussionReply2 = new DiscussionReply();
        assertThat(discussionReply1).isNotEqualTo(discussionReply2);

        discussionReply2.setId(discussionReply1.getId());
        assertThat(discussionReply1).isEqualTo(discussionReply2);

        discussionReply2 = getDiscussionReplySample2();
        assertThat(discussionReply1).isNotEqualTo(discussionReply2);
    }

    @Test
    void discussionTest() {
        DiscussionReply discussionReply = getDiscussionReplyRandomSampleGenerator();
        Discussion discussionBack = getDiscussionRandomSampleGenerator();

        discussionReply.setDiscussion(discussionBack);
        assertThat(discussionReply.getDiscussion()).isEqualTo(discussionBack);

        discussionReply.discussion(null);
        assertThat(discussionReply.getDiscussion()).isNull();
    }

    @Test
    void parentTest() {
        DiscussionReply discussionReply = getDiscussionReplyRandomSampleGenerator();
        DiscussionReply discussionReplyBack = getDiscussionReplyRandomSampleGenerator();

        discussionReply.setParent(discussionReplyBack);
        assertThat(discussionReply.getParent()).isEqualTo(discussionReplyBack);

        discussionReply.parent(null);
        assertThat(discussionReply.getParent()).isNull();
    }
}
