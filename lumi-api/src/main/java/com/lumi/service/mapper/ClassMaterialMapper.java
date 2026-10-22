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
    @Mapping(target = "studyClass", source = "studyClass", qualifiedByName = "studyClassName")
    ClassMaterialDTO toDto(ClassMaterial s);

    @Named("studyClassName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    StudyClassDTO toDtoStudyClassName(StudyClass studyClass);
}
