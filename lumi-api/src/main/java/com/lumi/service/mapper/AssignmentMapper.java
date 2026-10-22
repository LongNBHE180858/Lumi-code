package com.lumi.service.mapper;

import com.lumi.domain.Assignment;
import com.lumi.domain.StudyClass;
import com.lumi.service.dto.AssignmentDTO;
import com.lumi.service.dto.StudyClassDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Assignment} and its DTO {@link AssignmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface AssignmentMapper extends EntityMapper<AssignmentDTO, Assignment> {
    @Mapping(target = "studyClass", source = "studyClass", qualifiedByName = "studyClassName")
    AssignmentDTO toDto(Assignment s);

    @Named("studyClassName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    StudyClassDTO toDtoStudyClassName(StudyClass studyClass);
}
