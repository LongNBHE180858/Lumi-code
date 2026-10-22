package com.lumi.service.mapper;

import com.lumi.domain.KnowledgeEdge;
import com.lumi.domain.KnowledgeNode;
import com.lumi.service.dto.KnowledgeEdgeDTO;
import com.lumi.service.dto.KnowledgeNodeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link KnowledgeEdge} and its DTO {@link KnowledgeEdgeDTO}.
 */
@Mapper(componentModel = "spring")
public interface KnowledgeEdgeMapper extends EntityMapper<KnowledgeEdgeDTO, KnowledgeEdge> {
    @Mapping(target = "fromNode", source = "fromNode", qualifiedByName = "knowledgeNodeTitle")
    @Mapping(target = "toNode", source = "toNode", qualifiedByName = "knowledgeNodeTitle")
    KnowledgeEdgeDTO toDto(KnowledgeEdge s);

    @Named("knowledgeNodeTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    KnowledgeNodeDTO toDtoKnowledgeNodeTitle(KnowledgeNode knowledgeNode);
}
