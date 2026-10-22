package com.lumi.service.mapper;

import com.lumi.domain.Flashcard;
import com.lumi.domain.FlashcardReview;
import com.lumi.domain.User;
import com.lumi.service.dto.FlashcardDTO;
import com.lumi.service.dto.FlashcardReviewDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FlashcardReview} and its DTO {@link FlashcardReviewDTO}.
 */
@Mapper(componentModel = "spring")
public interface FlashcardReviewMapper extends EntityMapper<FlashcardReviewDTO, FlashcardReview> {
    @Mapping(target = "student", source = "student", qualifiedByName = "userLogin")
    @Mapping(target = "flashcard", source = "flashcard", qualifiedByName = "flashcardId")
    FlashcardReviewDTO toDto(FlashcardReview s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("flashcardId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FlashcardDTO toDtoFlashcardId(Flashcard flashcard);
}
