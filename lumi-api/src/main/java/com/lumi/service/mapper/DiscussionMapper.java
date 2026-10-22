package com.lumi.service.mapper;

import com.lumi.domain.Discussion;
import com.lumi.domain.Lesson;
import com.lumi.domain.User;
import com.lumi.service.dto.DiscussionDTO;
import com.lumi.service.dto.LessonDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Discussion} and its DTO {@link DiscussionDTO}.
 */
@Mapper(componentModel = "spring")
public interface DiscussionMapper extends EntityMapper<DiscussionDTO, Discussion> {
    @Mapping(target = "student", source = "student", qualifiedByName = "userLogin")
    @Mapping(target = "lesson", source = "lesson", qualifiedByName = "lessonTitle")
    DiscussionDTO toDto(Discussion s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("lessonTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    LessonDTO toDtoLessonTitle(Lesson lesson);
}
