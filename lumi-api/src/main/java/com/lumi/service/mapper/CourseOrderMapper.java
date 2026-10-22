package com.lumi.service.mapper;

import com.lumi.domain.Coupon;
import com.lumi.domain.Course;
import com.lumi.domain.CourseOrder;
import com.lumi.domain.SystemPlan;
import com.lumi.domain.User;
import com.lumi.service.dto.CouponDTO;
import com.lumi.service.dto.CourseDTO;
import com.lumi.service.dto.CourseOrderDTO;
import com.lumi.service.dto.SystemPlanDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CourseOrder} and its DTO {@link CourseOrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface CourseOrderMapper extends EntityMapper<CourseOrderDTO, CourseOrder> {
    @Mapping(target = "student", source = "student", qualifiedByName = "userLogin")
    @Mapping(target = "course", source = "course", qualifiedByName = "courseTitle")
    @Mapping(target = "plan", source = "plan", qualifiedByName = "systemPlanName")
    @Mapping(target = "coupon", source = "coupon", qualifiedByName = "couponCode")
    CourseOrderDTO toDto(CourseOrder s);

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

    @Named("systemPlanName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SystemPlanDTO toDtoSystemPlanName(SystemPlan systemPlan);

    @Named("couponCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    CouponDTO toDtoCouponCode(Coupon coupon);
}
