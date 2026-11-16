package com.lumi.service.mapper;

import com.lumi.domain.StudyClass;
import com.lumi.service.dto.studyClass.CreateStudyClassRequestDTO;
import com.lumi.service.dto.studyClass.InstructorClassListItemDTO;
import com.lumi.service.dto.studyClass.StudyClassDetailDTO;
import com.lumi.service.dto.studyClass.UpdateStudyClassRequestDTO;
import java.util.List;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StudyClass} and custom DTOs.
 */
@Mapper(componentModel = "spring")
public interface StudyClassMapper {
    StudyClass toStudyClass(CreateStudyClassRequestDTO request);
    StudyClassDetailDTO toStudyClassDetail(StudyClass studyClass);

    @Mapping(target = "subjectId", source = "studyClass.subject.id")
    @Mapping(target = "subjectName", source = "studyClass.subject.name")
    @Mapping(target = "gradeId", source = "studyClass.grade.id")
    @Mapping(target = "gradeName", source = "studyClass.grade.name")
    InstructorClassListItemDTO toInstructorClassList(StudyClass studyClass, long studentCount);

    void updateStudyClass(@MappingTarget StudyClass studyClass, UpdateStudyClassRequestDTO update);
    List<StudyClassDetailDTO> toStudyClassDetailList(List<StudyClass> classes);
}
