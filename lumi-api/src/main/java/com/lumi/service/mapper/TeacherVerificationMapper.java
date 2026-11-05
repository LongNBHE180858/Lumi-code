package com.lumi.service.mapper;

import com.lumi.domain.TeacherVerification;
import com.lumi.domain.User;
import com.lumi.service.dto.TeacherVerificationDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TeacherVerification} and its DTO {@link TeacherVerificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface TeacherVerificationMapper extends EntityMapper<TeacherVerificationDTO, TeacherVerification> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "reviewedBy", source = "reviewedBy", qualifiedByName = "userLogin")
    TeacherVerificationDTO toDto(TeacherVerification s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
