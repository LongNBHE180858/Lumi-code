package com.lumi.service.mapper;

import com.lumi.domain.Flashcard;
import com.lumi.domain.FlashcardDeck;
import com.lumi.service.dto.FlashcardDTO;
import com.lumi.service.dto.FlashcardDeckDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Flashcard} and its DTO {@link FlashcardDTO}.
 */
@Mapper(componentModel = "spring")
public interface FlashcardMapper extends EntityMapper<FlashcardDTO, Flashcard> {
    @Mapping(target = "deck", source = "deck", qualifiedByName = "flashcardDeckTitle")
    FlashcardDTO toDto(Flashcard s);

    @Named("flashcardDeckTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    FlashcardDeckDTO toDtoFlashcardDeckTitle(FlashcardDeck flashcardDeck);
}
