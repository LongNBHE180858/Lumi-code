package com.lumi.service.mapper;

import com.lumi.domain.AiConversation;
import com.lumi.domain.LearningDocument;
import com.lumi.domain.Lesson;
import com.lumi.domain.User;
import com.lumi.service.dto.AiConversationDTO;
import com.lumi.service.dto.LearningDocumentDTO;
import com.lumi.service.dto.LessonDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AiConversation} and its DTO {@link AiConversationDTO}.
 */
@Mapper(componentModel = "spring")
public interface AiConversationMapper extends EntityMapper<AiConversationDTO, AiConversation> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "lesson", source = "lesson", qualifiedByName = "lessonId")
    @Mapping(target = "document", source = "document", qualifiedByName = "learningDocumentId")
    AiConversationDTO toDto(AiConversation s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("lessonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LessonDTO toDtoLessonId(Lesson lesson);

    @Named("learningDocumentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LearningDocumentDTO toDtoLearningDocumentId(LearningDocument learningDocument);
}
