package com.lumi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FlashcardDeckDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FlashcardDeckDTO.class);
        FlashcardDeckDTO flashcardDeckDTO1 = new FlashcardDeckDTO();
        flashcardDeckDTO1.setId(1L);
        FlashcardDeckDTO flashcardDeckDTO2 = new FlashcardDeckDTO();
        assertThat(flashcardDeckDTO1).isNotEqualTo(flashcardDeckDTO2);
        flashcardDeckDTO2.setId(flashcardDeckDTO1.getId());
        assertThat(flashcardDeckDTO1).isEqualTo(flashcardDeckDTO2);
        flashcardDeckDTO2.setId(2L);
        assertThat(flashcardDeckDTO1).isNotEqualTo(flashcardDeckDTO2);
        flashcardDeckDTO1.setId(null);
        assertThat(flashcardDeckDTO1).isNotEqualTo(flashcardDeckDTO2);
    }
}
