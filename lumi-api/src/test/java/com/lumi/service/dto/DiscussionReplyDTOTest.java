package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DiscussionReplyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DiscussionReplyDTO.class);
        DiscussionReplyDTO discussionReplyDTO1 = new DiscussionReplyDTO();
        discussionReplyDTO1.setId(1L);
        DiscussionReplyDTO discussionReplyDTO2 = new DiscussionReplyDTO();
        assertThat(discussionReplyDTO1).isNotEqualTo(discussionReplyDTO2);
        discussionReplyDTO2.setId(discussionReplyDTO1.getId());
        assertThat(discussionReplyDTO1).isEqualTo(discussionReplyDTO2);
        discussionReplyDTO2.setId(2L);
        assertThat(discussionReplyDTO1).isNotEqualTo(discussionReplyDTO2);
        discussionReplyDTO1.setId(null);
        assertThat(discussionReplyDTO1).isNotEqualTo(discussionReplyDTO2);
    }
}
