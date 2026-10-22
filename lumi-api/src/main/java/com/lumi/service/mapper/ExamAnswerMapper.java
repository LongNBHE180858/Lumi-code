package com.lumi.service.mapper;

import com.lumi.domain.ExamAnswer;
import com.lumi.domain.ExamAttempt;
import com.lumi.domain.Question;
import com.lumi.domain.QuestionOption;
import com.lumi.service.dto.ExamAnswerDTO;
import com.lumi.service.dto.ExamAttemptDTO;
import com.lumi.service.dto.QuestionDTO;
import com.lumi.service.dto.QuestionOptionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ExamAnswer} and its DTO {@link ExamAnswerDTO}.
 */
@Mapper(componentModel = "spring")
public interface ExamAnswerMapper extends EntityMapper<ExamAnswerDTO, ExamAnswer> {
    @Mapping(target = "attempt", source = "attempt", qualifiedByName = "examAttemptId")
    @Mapping(target = "question", source = "question", qualifiedByName = "questionId")
    @Mapping(target = "option", source = "option", qualifiedByName = "questionOptionId")
    ExamAnswerDTO toDto(ExamAnswer s);

    @Named("examAttemptId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ExamAttemptDTO toDtoExamAttemptId(ExamAttempt examAttempt);

    @Named("questionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    QuestionDTO toDtoQuestionId(Question question);

    @Named("questionOptionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    QuestionOptionDTO toDtoQuestionOptionId(QuestionOption questionOption);
}
