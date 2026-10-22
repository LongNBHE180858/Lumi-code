package com.lumi.service.mapper;

import com.lumi.domain.Course;
import com.lumi.domain.CourseReviewRequest;
import com.lumi.domain.User;
import com.lumi.service.dto.CourseDTO;
import com.lumi.service.dto.CourseReviewRequestDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CourseReviewRequest} and its DTO {@link CourseReviewRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface CourseReviewRequestMapper extends EntityMapper<CourseReviewRequestDTO, CourseReviewRequest> {
    @Mapping(target = "submittedBy", source = "submittedBy", qualifiedByName = "userLogin")
    @Mapping(target = "course", source = "course", qualifiedByName = "courseTitle")
    @Mapping(target = "reviewedBy", source = "reviewedBy", qualifiedByName = "userLogin")
    CourseReviewRequestDTO toDto(CourseReviewRequest s);

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
