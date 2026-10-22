package com.lumi.service.mapper;

import com.lumi.domain.FlashcardDeck;
import com.lumi.domain.Subject;
import com.lumi.domain.User;
import com.lumi.service.dto.FlashcardDeckDTO;
import com.lumi.service.dto.SubjectDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FlashcardDeck} and its DTO {@link FlashcardDeckDTO}.
 */
@Mapper(componentModel = "spring")
public interface FlashcardDeckMapper extends EntityMapper<FlashcardDeckDTO, FlashcardDeck> {
    @Mapping(target = "owner", source = "owner", qualifiedByName = "userLogin")
    @Mapping(target = "subject", source = "subject", qualifiedByName = "subjectName")
    FlashcardDeckDTO toDto(FlashcardDeck s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("subjectName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SubjectDTO toDtoSubjectName(Subject subject);
}
