package com.lumi.service.mapper;

import com.lumi.domain.Grade;
import com.lumi.domain.StudyClass;
import com.lumi.domain.Subject;
import com.lumi.domain.User;
import com.lumi.service.dto.GradeDTO;
import com.lumi.service.dto.StudyClassDTO;
import com.lumi.service.dto.SubjectDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StudyClass} and its DTO {@link StudyClassDTO}.
 */
@Mapper(componentModel = "spring")
public interface StudyClassMapper extends EntityMapper<StudyClassDTO, StudyClass> {
    @Mapping(target = "instructor", source = "instructor", qualifiedByName = "userLogin")
    @Mapping(target = "subject", source = "subject", qualifiedByName = "subjectName")
    @Mapping(target = "grade", source = "grade", qualifiedByName = "gradeName")
    StudyClassDTO toDto(StudyClass s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("subjectName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SubjectDTO toDtoSubjectName(Subject subject);

    @Named("gradeName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    GradeDTO toDtoGradeName(Grade grade);
}
