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
    @Mapping(target = "course", source = "course", qualifiedByName = "courseTitle")
    ChapterDTO toDto(Chapter s);

    @Named("courseTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    CourseDTO toDtoCourseTitle(Course course);
}
