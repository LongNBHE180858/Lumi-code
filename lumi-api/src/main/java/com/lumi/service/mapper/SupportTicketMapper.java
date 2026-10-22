package com.lumi.service.mapper;

import com.lumi.domain.SupportTicket;
import com.lumi.domain.User;
import com.lumi.service.dto.SupportTicketDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SupportTicket} and its DTO {@link SupportTicketDTO}.
 */
@Mapper(componentModel = "spring")
public interface SupportTicketMapper extends EntityMapper<SupportTicketDTO, SupportTicket> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    SupportTicketDTO toDto(SupportTicket s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
