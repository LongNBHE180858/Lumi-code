package com.lumi.service.mapper;

import com.lumi.domain.AuthSession;
import com.lumi.domain.User;
import com.lumi.service.dto.AuthSessionDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AuthSession} and its DTO {@link AuthSessionDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuthSessionMapper extends EntityMapper<AuthSessionDTO, AuthSession> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    AuthSessionDTO toDto(AuthSession s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
