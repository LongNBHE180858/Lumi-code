package com.lumi.service.mapper;

import com.lumi.domain.AiGeneratedContent;
import com.lumi.domain.Course;
import com.lumi.domain.LearningDocument;
import com.lumi.domain.Lesson;
import com.lumi.domain.User;
import com.lumi.service.dto.AiGeneratedContentDTO;
import com.lumi.service.dto.CourseDTO;
import com.lumi.service.dto.LearningDocumentDTO;
import com.lumi.service.dto.LessonDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AiGeneratedContent} and its DTO {@link AiGeneratedContentDTO}.
 */
@Mapper(componentModel = "spring")
public interface AiGeneratedContentMapper extends EntityMapper<AiGeneratedContentDTO, AiGeneratedContent> {
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "userLogin")
    @Mapping(target = "document", source = "document", qualifiedByName = "learningDocumentId")
    @Mapping(target = "lesson", source = "lesson", qualifiedByName = "lessonId")
    @Mapping(target = "reviewedBy", source = "reviewedBy", qualifiedByName = "userLogin")
    @Mapping(target = "course", source = "course", qualifiedByName = "courseId")
    AiGeneratedContentDTO toDto(AiGeneratedContent s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("learningDocumentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LearningDocumentDTO toDtoLearningDocumentId(LearningDocument learningDocument);

    @Named("lessonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LessonDTO toDtoLessonId(Lesson lesson);

    @Named("courseId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CourseDTO toDtoCourseId(Course course);
}
