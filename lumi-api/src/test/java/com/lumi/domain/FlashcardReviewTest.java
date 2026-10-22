package com.lumi.domain;

import static com.lumi.domain.FlashcardReviewTestSamples.*;
import static com.lumi.domain.FlashcardTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FlashcardReviewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FlashcardReview.class);
        FlashcardReview flashcardReview1 = getFlashcardReviewSample1();
        FlashcardReview flashcardReview2 = new FlashcardReview();
        assertThat(flashcardReview1).isNotEqualTo(flashcardReview2);

        flashcardReview2.setId(flashcardReview1.getId());
        assertThat(flashcardReview1).isEqualTo(flashcardReview2);

        flashcardReview2 = getFlashcardReviewSample2();
        assertThat(flashcardReview1).isNotEqualTo(flashcardReview2);
    }

    @Test
    void flashcardTest() {
        FlashcardReview flashcardReview = getFlashcardReviewRandomSampleGenerator();
        Flashcard flashcardBack = getFlashcardRandomSampleGenerator();

        flashcardReview.setFlashcard(flashcardBack);
        assertThat(flashcardReview.getFlashcard()).isEqualTo(flashcardBack);

        flashcardReview.flashcard(null);
        assertThat(flashcardReview.getFlashcard()).isNull();
    }
}
