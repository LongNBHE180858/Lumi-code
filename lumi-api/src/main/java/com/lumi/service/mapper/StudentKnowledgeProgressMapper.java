package com.lumi.service.mapper;

import com.lumi.domain.KnowledgeNode;
import com.lumi.domain.StudentKnowledgeProgress;
import com.lumi.domain.User;
import com.lumi.service.dto.KnowledgeNodeDTO;
import com.lumi.service.dto.StudentKnowledgeProgressDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StudentKnowledgeProgress} and its DTO {@link StudentKnowledgeProgressDTO}.
 */
@Mapper(componentModel = "spring")
public interface StudentKnowledgeProgressMapper extends EntityMapper<StudentKnowledgeProgressDTO, StudentKnowledgeProgress> {
    @Mapping(target = "student", source = "student", qualifiedByName = "userLogin")
    @Mapping(target = "knowledgeNode", source = "knowledgeNode", qualifiedByName = "knowledgeNodeId")
    StudentKnowledgeProgressDTO toDto(StudentKnowledgeProgress s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("knowledgeNodeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    KnowledgeNodeDTO toDtoKnowledgeNodeId(KnowledgeNode knowledgeNode);
}
