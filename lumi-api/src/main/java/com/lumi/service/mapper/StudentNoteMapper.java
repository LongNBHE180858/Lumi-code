package com.lumi.service.mapper;

import com.lumi.domain.Lesson;
import com.lumi.domain.StudentNote;
import com.lumi.domain.User;
import com.lumi.service.dto.LessonDTO;
import com.lumi.service.dto.StudentNoteDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StudentNote} and its DTO {@link StudentNoteDTO}.
 */
@Mapper(componentModel = "spring")
public interface StudentNoteMapper extends EntityMapper<StudentNoteDTO, StudentNote> {
    @Mapping(target = "student", source = "student", qualifiedByName = "userLogin")
    @Mapping(target = "lesson", source = "lesson", qualifiedByName = "lessonTitle")
    StudentNoteDTO toDto(StudentNote s);

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
