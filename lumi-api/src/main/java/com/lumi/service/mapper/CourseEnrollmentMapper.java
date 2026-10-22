package com.lumi.service.mapper;

import com.lumi.domain.Course;
import com.lumi.domain.CourseEnrollment;
import com.lumi.domain.User;
import com.lumi.service.dto.CourseDTO;
import com.lumi.service.dto.CourseEnrollmentDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CourseEnrollment} and its DTO {@link CourseEnrollmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface CourseEnrollmentMapper extends EntityMapper<CourseEnrollmentDTO, CourseEnrollment> {
    @Mapping(target = "student", source = "student", qualifiedByName = "userLogin")
    @Mapping(target = "course", source = "course", qualifiedByName = "courseTitle")
    CourseEnrollmentDTO toDto(CourseEnrollment s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("courseTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    CourseDTO toDtoCourseTitle(Course course);
}
