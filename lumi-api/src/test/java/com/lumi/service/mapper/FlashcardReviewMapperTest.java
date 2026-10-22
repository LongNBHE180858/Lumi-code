package com.lumi.service.mapper;

import static com.lumi.domain.FlashcardReviewAsserts.*;
import static com.lumi.domain.FlashcardReviewTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FlashcardReviewMapperTest {

    private FlashcardReviewMapper flashcardReviewMapper;

    @BeforeEach
    void setUp() {
        flashcardReviewMapper = new FlashcardReviewMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFlashcardReviewSample1();
        var actual = flashcardReviewMapper.toEntity(flashcardReviewMapper.toDto(expected));
        assertFlashcardReviewAllPropertiesEquals(expected, actual);
    }
}
