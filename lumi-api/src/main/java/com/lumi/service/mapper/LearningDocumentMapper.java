package com.lumi.service.mapper;

import com.lumi.domain.ClassMaterial;
import com.lumi.domain.CourseMaterial;
import com.lumi.domain.Grade;
import com.lumi.domain.LearningDocument;
import com.lumi.domain.Subject;
import com.lumi.domain.User;
import com.lumi.service.dto.ClassMaterialDTO;
import com.lumi.service.dto.CourseMaterialDTO;
import com.lumi.service.dto.GradeDTO;
import com.lumi.service.dto.LearningDocumentDTO;
import com.lumi.service.dto.SubjectDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LearningDocument} and its DTO {@link LearningDocumentDTO}.
 */
@Mapper(componentModel = "spring")
public interface LearningDocumentMapper extends EntityMapper<LearningDocumentDTO, LearningDocument> {
    @Mapping(target = "courseMaterial", source = "courseMaterial", qualifiedByName = "courseMaterialId")
    @Mapping(target = "classMaterial", source = "classMaterial", qualifiedByName = "classMaterialId")
    @Mapping(target = "uploadedBy", source = "uploadedBy", qualifiedByName = "userLogin")
    @Mapping(target = "subject", source = "subject", qualifiedByName = "subjectId")
    @Mapping(target = "grade", source = "grade", qualifiedByName = "gradeId")
    LearningDocumentDTO toDto(LearningDocument s);

    @Named("courseMaterialId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CourseMaterialDTO toDtoCourseMaterialId(CourseMaterial courseMaterial);

    @Named("classMaterialId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClassMaterialDTO toDtoClassMaterialId(ClassMaterial classMaterial);

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
    GradeDTO toDtoGradeId(Grade grade);
}
