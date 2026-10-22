package com.lumi.service.mapper;

import com.lumi.domain.AiConversation;
import com.lumi.domain.AiMessage;
import com.lumi.service.dto.AiConversationDTO;
import com.lumi.service.dto.AiMessageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AiMessage} and its DTO {@link AiMessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface AiMessageMapper extends EntityMapper<AiMessageDTO, AiMessage> {
    @Mapping(target = "conversation", source = "conversation", qualifiedByName = "aiConversationId")
    AiMessageDTO toDto(AiMessage s);

    @Named("aiConversationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AiConversationDTO toDtoAiConversationId(AiConversation aiConversation);
}
