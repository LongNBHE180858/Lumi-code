package com.lumi.service.mapper;

import com.lumi.domain.AuditLog;
import com.lumi.domain.User;
import com.lumi.service.dto.AuditLogDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AuditLog} and its DTO {@link AuditLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuditLogMapper extends EntityMapper<AuditLogDTO, AuditLog> {
    @Mapping(target = "actor", source = "actor", qualifiedByName = "userLogin")
    AuditLogDTO toDto(AuditLog s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
