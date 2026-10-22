package com.lumi.service.mapper;

import com.lumi.domain.Question;
import com.lumi.domain.QuestionBank;
import com.lumi.service.dto.QuestionBankDTO;
import com.lumi.service.dto.QuestionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Question} and its DTO {@link QuestionDTO}.
 */
@Mapper(componentModel = "spring")
public interface QuestionMapper extends EntityMapper<QuestionDTO, Question> {
    @Mapping(target = "bank", source = "bank", qualifiedByName = "questionBankName")
    QuestionDTO toDto(Question s);

    @Named("questionBankName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    QuestionBankDTO toDtoQuestionBankName(QuestionBank questionBank);
}
