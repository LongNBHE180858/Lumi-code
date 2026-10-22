package com.lumi.service.mapper;

import com.lumi.domain.Exam;
import com.lumi.domain.StudyClass;
import com.lumi.service.dto.ExamDTO;
import com.lumi.service.dto.StudyClassDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Exam} and its DTO {@link ExamDTO}.
 */
@Mapper(componentModel = "spring")
public interface ExamMapper extends EntityMapper<ExamDTO, Exam> {
    @Mapping(target = "studyClass", source = "studyClass", qualifiedByName = "studyClassName")
    ExamDTO toDto(Exam s);

    @Named("studyClassName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    StudyClassDTO toDtoStudyClassName(StudyClass studyClass);
}
