package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FlashcardReviewDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FlashcardReviewDTO.class);
        FlashcardReviewDTO flashcardReviewDTO1 = new FlashcardReviewDTO();
        flashcardReviewDTO1.setId(1L);
        FlashcardReviewDTO flashcardReviewDTO2 = new FlashcardReviewDTO();
        assertThat(flashcardReviewDTO1).isNotEqualTo(flashcardReviewDTO2);
        flashcardReviewDTO2.setId(flashcardReviewDTO1.getId());
        assertThat(flashcardReviewDTO1).isEqualTo(flashcardReviewDTO2);
        flashcardReviewDTO2.setId(2L);
        assertThat(flashcardReviewDTO1).isNotEqualTo(flashcardReviewDTO2);
        flashcardReviewDTO1.setId(null);
        assertThat(flashcardReviewDTO1).isNotEqualTo(flashcardReviewDTO2);
    }
}
