package com.lumi.service.mapper;

import com.lumi.domain.Grade;
import com.lumi.domain.StudyClass;
import com.lumi.domain.Subject;
import com.lumi.domain.User;
import com.lumi.service.dto.GradeDTO;
import com.lumi.service.dto.StudyClassDTO;
import com.lumi.service.dto.SubjectDTO;
import com.lumi.service.dto.UserDTO;
import com.lumi.service.dto.studyClass.CreateStudyClassRequestDTO;
import com.lumi.service.dto.studyClass.StudyClassDetailDTO;
import com.lumi.service.dto.studyClass.UpdateStudyClassRequestDTO;
import java.util.List;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StudyClass} and its DTO {@link StudyClassDTO}.
 */
@Mapper(componentModel = "spring")
public interface StudyClassMapper extends EntityMapper<StudyClassDTO, StudyClass> {
    @Mapping(target = "instructor", source = "instructor", qualifiedByName = "userLogin")
    @Mapping(target = "subject", source = "subject", qualifiedByName = "subjectId")
    @Mapping(target = "grade", source = "grade", qualifiedByName = "gradeId")
    StudyClassDTO toDto(StudyClass s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("subjectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SubjectDTO toDtoSubjectId(Subject subject);

    @Named("gradeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    GradeDTO toDtoGradeName(Grade grade);

    StudyClass toStudyClass(CreateStudyClassRequestDTO request);
    StudyClassDetailDTO toStudyClassDetail(StudyClass studyClass);
    void updateStudyClass(@MappingTarget StudyClass studyClass, UpdateStudyClassRequestDTO update);
    List<StudyClassDetailDTO> toStudyClassDetailList(List<StudyClass> classes);
}
