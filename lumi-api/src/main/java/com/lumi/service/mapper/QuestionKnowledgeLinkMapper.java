package com.lumi.service.mapper;

import com.lumi.domain.KnowledgeNode;
import com.lumi.domain.Question;
import com.lumi.domain.QuestionKnowledgeLink;
import com.lumi.service.dto.KnowledgeNodeDTO;
import com.lumi.service.dto.QuestionDTO;
import com.lumi.service.dto.QuestionKnowledgeLinkDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link QuestionKnowledgeLink} and its DTO {@link QuestionKnowledgeLinkDTO}.
 */
@Mapper(componentModel = "spring")
public interface QuestionKnowledgeLinkMapper extends EntityMapper<QuestionKnowledgeLinkDTO, QuestionKnowledgeLink> {
    @Mapping(target = "question", source = "question", qualifiedByName = "questionId")
    @Mapping(target = "knowledgeNode", source = "knowledgeNode", qualifiedByName = "knowledgeNodeId")
    QuestionKnowledgeLinkDTO toDto(QuestionKnowledgeLink s);

    @Named("questionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    QuestionDTO toDtoQuestionId(Question question);

    @Named("knowledgeNodeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    KnowledgeNodeDTO toDtoKnowledgeNodeId(KnowledgeNode knowledgeNode);
}
