package com.lumi.service.mapper;

import com.lumi.domain.Course;
import com.lumi.domain.CourseEnrollment;
import com.lumi.domain.Lesson;
import com.lumi.domain.User;
import com.lumi.service.dto.CourseDTO;
import com.lumi.service.dto.CourseEnrollmentDTO;
import com.lumi.service.dto.LessonDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CourseEnrollment} and its DTO {@link CourseEnrollmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface CourseEnrollmentMapper extends EntityMapper<CourseEnrollmentDTO, CourseEnrollment> {
    @Mapping(target = "course", source = "course", qualifiedByName = "courseId")
    @Mapping(target = "student", source = "student", qualifiedByName = "userLogin")
    @Mapping(target = "lastAccessedLesson", source = "lastAccessedLesson", qualifiedByName = "lessonId")
    CourseEnrollmentDTO toDto(CourseEnrollment s);

    @Named("courseId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CourseDTO toDtoCourseId(Course course);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("lessonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LessonDTO toDtoLessonId(Lesson lesson);
}
