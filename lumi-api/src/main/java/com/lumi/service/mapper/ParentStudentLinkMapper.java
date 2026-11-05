package com.lumi.service.mapper;

import com.lumi.domain.ParentStudentLink;
import com.lumi.domain.User;
import com.lumi.service.dto.ParentStudentLinkDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ParentStudentLink} and its DTO {@link ParentStudentLinkDTO}.
 */
@Mapper(componentModel = "spring")
public interface ParentStudentLinkMapper extends EntityMapper<ParentStudentLinkDTO, ParentStudentLink> {
    @Mapping(target = "parent", source = "parent", qualifiedByName = "userLogin")
    @Mapping(target = "student", source = "student", qualifiedByName = "userLogin")
    @Mapping(target = "confirmedBy", source = "confirmedBy", qualifiedByName = "userLogin")
    @Mapping(target = "guardianVerifiedBy", source = "guardianVerifiedBy", qualifiedByName = "userLogin")
    ParentStudentLinkDTO toDto(ParentStudentLink s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
