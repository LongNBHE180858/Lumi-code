package com.lumi.service.mapper;

import com.lumi.domain.Category;
import com.lumi.domain.Course;
import com.lumi.domain.Grade;
import com.lumi.domain.Subject;
import com.lumi.domain.User;
import com.lumi.service.dto.CategoryDTO;
import com.lumi.service.dto.CourseDTO;
import com.lumi.service.dto.GradeDTO;
import com.lumi.service.dto.SubjectDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Course} and its DTO {@link CourseDTO}.
 */
@Mapper(componentModel = "spring")
public interface CourseMapper extends EntityMapper<CourseDTO, Course> {
    @Mapping(target = "author", source = "author", qualifiedByName = "userLogin")
    @Mapping(target = "authorDisplayName", source = "author", qualifiedByName = "userDisplayName")
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryName")
    @Mapping(target = "subject", source = "subject", qualifiedByName = "subjectName")
    @Mapping(target = "grade", source = "grade", qualifiedByName = "gradeName")
    CourseDTO toDto(Course s);

    @Override
    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "version", ignore = true)
    void partialUpdate(@MappingTarget Course entity, CourseDTO dto);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("userDisplayName")
    default String userDisplayName(User user) {
        if (user == null) return null;
        String name = java.util.stream.Stream.of(user.getFirstName(), user.getLastName())
            .filter(value -> value != null && !value.isBlank())
            .map(String::trim)
            .collect(java.util.stream.Collectors.joining(" "));
        return name.isEmpty() ? user.getLogin() : name;
    }

    @Named("categoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CategoryDTO toDtoCategoryName(Category category);

    @Named("subjectName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SubjectDTO toDtoSubjectName(Subject subject);

    @Named("gradeName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    GradeDTO toDtoGradeName(Grade grade);
}
