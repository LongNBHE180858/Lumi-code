package com.lumi.service.mapper;

import com.lumi.domain.Coupon;
import com.lumi.domain.User;
import com.lumi.service.dto.CouponDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Coupon} and its DTO {@link CouponDTO}.
 */
@Mapper(componentModel = "spring")
public interface CouponMapper extends EntityMapper<CouponDTO, Coupon> {
    @Mapping(target = "instructor", source = "instructor", qualifiedByName = "userLogin")
    CouponDTO toDto(Coupon s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
