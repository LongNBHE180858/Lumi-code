package com.lumi.service.mapper;

import com.lumi.domain.Course;
import com.lumi.domain.User;
import com.lumi.domain.Wishlist;
import com.lumi.service.dto.CourseDTO;
import com.lumi.service.dto.UserDTO;
import com.lumi.service.dto.WishlistDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Wishlist} and its DTO {@link WishlistDTO}.
 */
@Mapper(componentModel = "spring")
public interface WishlistMapper extends EntityMapper<WishlistDTO, Wishlist> {
    @Mapping(target = "student", source = "student", qualifiedByName = "userLogin")
    @Mapping(target = "course", source = "course", qualifiedByName = "courseTitle")
    WishlistDTO toDto(Wishlist s);

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
