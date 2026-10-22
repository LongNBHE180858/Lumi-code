package com.lumi.service.mapper;

import com.lumi.domain.Exam;
import com.lumi.domain.ExamQuestion;
import com.lumi.domain.Question;
import com.lumi.service.dto.ExamDTO;
import com.lumi.service.dto.ExamQuestionDTO;
import com.lumi.service.dto.QuestionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ExamQuestion} and its DTO {@link ExamQuestionDTO}.
 */
@Mapper(componentModel = "spring")
public interface ExamQuestionMapper extends EntityMapper<ExamQuestionDTO, ExamQuestion> {
    @Mapping(target = "exam", source = "exam", qualifiedByName = "examTitle")
    @Mapping(target = "question", source = "question", qualifiedByName = "questionId")
    ExamQuestionDTO toDto(ExamQuestion s);

    @Named("examTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    ExamDTO toDtoExamTitle(Exam exam);

    @Named("questionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    QuestionDTO toDtoQuestionId(Question question);
}
