package com.lumi.service.mapper;

import com.lumi.domain.Discussion;
import com.lumi.domain.DiscussionReply;
import com.lumi.domain.User;
import com.lumi.service.dto.DiscussionDTO;
import com.lumi.service.dto.DiscussionReplyDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DiscussionReply} and its DTO {@link DiscussionReplyDTO}.
 */
@Mapper(componentModel = "spring")
public interface DiscussionReplyMapper extends EntityMapper<DiscussionReplyDTO, DiscussionReply> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "discussion", source = "discussion", qualifiedByName = "discussionTitle")
    @Mapping(target = "parent", source = "parent", qualifiedByName = "discussionReplyId")
    DiscussionReplyDTO toDto(DiscussionReply s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("discussionTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    DiscussionDTO toDtoDiscussionTitle(Discussion discussion);

    @Named("discussionReplyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DiscussionReplyDTO toDtoDiscussionReplyId(DiscussionReply discussionReply);
}
