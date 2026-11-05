package com.lumi.service.mapper;

import com.lumi.domain.ClassMaterial;
import com.lumi.domain.StudyClass;
import com.lumi.service.dto.ClassMaterialDTO;
import com.lumi.service.dto.StudyClassDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ClassMaterial} and its DTO {@link ClassMaterialDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClassMaterialMapper extends EntityMapper<ClassMaterialDTO, ClassMaterial> {
    @Mapping(target = "studyClass", source = "studyClass", qualifiedByName = "studyClassId")
    ClassMaterialDTO toDto(ClassMaterial s);

    @Named("studyClassId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StudyClassDTO toDtoStudyClassId(StudyClass studyClass);
}
