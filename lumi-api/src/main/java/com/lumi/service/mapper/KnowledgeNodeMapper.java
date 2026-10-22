package com.lumi.service.mapper;

import com.lumi.domain.KnowledgeNode;
import com.lumi.domain.Subject;
import com.lumi.service.dto.KnowledgeNodeDTO;
import com.lumi.service.dto.SubjectDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link KnowledgeNode} and its DTO {@link KnowledgeNodeDTO}.
 */
@Mapper(componentModel = "spring")
public interface KnowledgeNodeMapper extends EntityMapper<KnowledgeNodeDTO, KnowledgeNode> {
    @Mapping(target = "subject", source = "subject", qualifiedByName = "subjectName")
    KnowledgeNodeDTO toDto(KnowledgeNode s);

    @Named("subjectName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SubjectDTO toDtoSubjectName(Subject subject);
}
