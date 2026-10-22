package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DiscussionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DiscussionDTO.class);
        DiscussionDTO discussionDTO1 = new DiscussionDTO();
        discussionDTO1.setId(1L);
        DiscussionDTO discussionDTO2 = new DiscussionDTO();
        assertThat(discussionDTO1).isNotEqualTo(discussionDTO2);
        discussionDTO2.setId(discussionDTO1.getId());
        assertThat(discussionDTO1).isEqualTo(discussionDTO2);
        discussionDTO2.setId(2L);
        assertThat(discussionDTO1).isNotEqualTo(discussionDTO2);
        discussionDTO1.setId(null);
        assertThat(discussionDTO1).isNotEqualTo(discussionDTO2);
    }
}
