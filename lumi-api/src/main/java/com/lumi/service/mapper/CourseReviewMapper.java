package com.lumi.service.mapper;

import com.lumi.domain.Course;
import com.lumi.domain.CourseReview;
import com.lumi.domain.User;
import com.lumi.service.dto.CourseDTO;
import com.lumi.service.dto.CourseReviewDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CourseReview} and its DTO {@link CourseReviewDTO}.
 */
@Mapper(componentModel = "spring")
public interface CourseReviewMapper extends EntityMapper<CourseReviewDTO, CourseReview> {
    @Mapping(target = "student", source = "student", qualifiedByName = "userLogin")
    @Mapping(target = "course", source = "course", qualifiedByName = "courseTitle")
    CourseReviewDTO toDto(CourseReview s);

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
