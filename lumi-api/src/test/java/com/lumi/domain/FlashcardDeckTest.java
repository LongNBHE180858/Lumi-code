package com.lumi.domain;

import static com.lumi.domain.FlashcardDeckTestSamples.*;
import static com.lumi.domain.SubjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FlashcardDeckTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FlashcardDeck.class);
        FlashcardDeck flashcardDeck1 = getFlashcardDeckSample1();
        FlashcardDeck flashcardDeck2 = new FlashcardDeck();
        assertThat(flashcardDeck1).isNotEqualTo(flashcardDeck2);

        flashcardDeck2.setId(flashcardDeck1.getId());
        assertThat(flashcardDeck1).isEqualTo(flashcardDeck2);

        flashcardDeck2 = getFlashcardDeckSample2();
        assertThat(flashcardDeck1).isNotEqualTo(flashcardDeck2);
    }

    @Test
    void subjectTest() {
        FlashcardDeck flashcardDeck = getFlashcardDeckRandomSampleGenerator();
        Subject subjectBack = getSubjectRandomSampleGenerator();

        flashcardDeck.setSubject(subjectBack);
        assertThat(flashcardDeck.getSubject()).isEqualTo(subjectBack);

        flashcardDeck.subject(null);
        assertThat(flashcardDeck.getSubject()).isNull();
    }
}
