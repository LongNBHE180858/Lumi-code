package com.lumi.service.mapper;

import com.lumi.domain.SupportTicket;
import com.lumi.domain.TicketMessage;
import com.lumi.domain.User;
import com.lumi.service.dto.SupportTicketDTO;
import com.lumi.service.dto.TicketMessageDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TicketMessage} and its DTO {@link TicketMessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface TicketMessageMapper extends EntityMapper<TicketMessageDTO, TicketMessage> {
    @Mapping(target = "sender", source = "sender", qualifiedByName = "userLogin")
    @Mapping(target = "ticket", source = "ticket", qualifiedByName = "supportTicketSubject")
    TicketMessageDTO toDto(TicketMessage s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("supportTicketSubject")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "subject", source = "subject")
    SupportTicketDTO toDtoSupportTicketSubject(SupportTicket supportTicket);
}
