package com.lumi.service.mapper;

import com.lumi.domain.Question;
import com.lumi.domain.QuestionOption;
import com.lumi.service.dto.QuestionDTO;
import com.lumi.service.dto.QuestionOptionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link QuestionOption} and its DTO {@link QuestionOptionDTO}.
 */
@Mapper(componentModel = "spring")
public interface QuestionOptionMapper extends EntityMapper<QuestionOptionDTO, QuestionOption> {
    @Mapping(target = "question", source = "question", qualifiedByName = "questionId")
    QuestionOptionDTO toDto(QuestionOption s);

    @Named("questionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    QuestionDTO toDtoQuestionId(Question question);
}
