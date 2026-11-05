package com.lumi.service.mapper;

import com.lumi.domain.Chapter;
import com.lumi.domain.Course;
import com.lumi.service.dto.ChapterDTO;
import com.lumi.service.dto.CourseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Chapter} and its DTO {@link ChapterDTO}.
 */
@Mapper(componentModel = "spring")
public interface ChapterMapper extends EntityMapper<ChapterDTO, Chapter> {
    @Mapping(target = "course", source = "course", qualifiedByName = "courseId")
    ChapterDTO toDto(Chapter s);

    @Named("courseId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CourseDTO toDtoCourseId(Course course);
}
