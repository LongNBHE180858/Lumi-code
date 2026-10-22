package com.lumi.domain;

import static com.lumi.domain.FlashcardDeckTestSamples.*;
import static com.lumi.domain.FlashcardTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FlashcardTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Flashcard.class);
        Flashcard flashcard1 = getFlashcardSample1();
        Flashcard flashcard2 = new Flashcard();
        assertThat(flashcard1).isNotEqualTo(flashcard2);

        flashcard2.setId(flashcard1.getId());
        assertThat(flashcard1).isEqualTo(flashcard2);

        flashcard2 = getFlashcardSample2();
        assertThat(flashcard1).isNotEqualTo(flashcard2);
    }

    @Test
    void deckTest() {
        Flashcard flashcard = getFlashcardRandomSampleGenerator();
        FlashcardDeck flashcardDeckBack = getFlashcardDeckRandomSampleGenerator();

        flashcard.setDeck(flashcardDeckBack);
        assertThat(flashcard.getDeck()).isEqualTo(flashcardDeckBack);

        flashcard.deck(null);
        assertThat(flashcard.getDeck()).isNull();
    }
}
