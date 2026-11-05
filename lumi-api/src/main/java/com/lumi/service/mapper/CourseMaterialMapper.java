package com.lumi.service.mapper;

import com.lumi.domain.CourseMaterial;
import com.lumi.domain.Lesson;
import com.lumi.service.dto.CourseMaterialDTO;
import com.lumi.service.dto.LessonDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CourseMaterial} and its DTO {@link CourseMaterialDTO}.
 */
@Mapper(componentModel = "spring")
public interface CourseMaterialMapper extends EntityMapper<CourseMaterialDTO, CourseMaterial> {
    @Mapping(target = "lesson", source = "lesson", qualifiedByName = "lessonId")
    CourseMaterialDTO toDto(CourseMaterial s);

    @Named("lessonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LessonDTO toDtoLessonId(Lesson lesson);
}
