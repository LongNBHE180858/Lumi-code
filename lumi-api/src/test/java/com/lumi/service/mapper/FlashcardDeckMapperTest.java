package com.lumi.service.mapper;

import static com.lumi.domain.FlashcardDeckAsserts.*;
import static com.lumi.domain.FlashcardDeckTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FlashcardDeckMapperTest {

    private FlashcardDeckMapper flashcardDeckMapper;

    @BeforeEach
    void setUp() {
        flashcardDeckMapper = new FlashcardDeckMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFlashcardDeckSample1();
        var actual = flashcardDeckMapper.toEntity(flashcardDeckMapper.toDto(expected));
        assertFlashcardDeckAllPropertiesEquals(expected, actual);
    }
}
