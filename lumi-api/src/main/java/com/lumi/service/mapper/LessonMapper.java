package com.lumi.service.mapper;

import com.lumi.domain.Chapter;
import com.lumi.domain.Lesson;
import com.lumi.service.dto.ChapterDTO;
import com.lumi.service.dto.LessonDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Lesson} and its DTO {@link LessonDTO}.
 */
@Mapper(componentModel = "spring")
public interface LessonMapper extends EntityMapper<LessonDTO, Lesson> {
    @Mapping(target = "chapter", source = "chapter", qualifiedByName = "chapterId")
    LessonDTO toDto(Lesson s);

    @Named("chapterId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ChapterDTO toDtoChapterId(Chapter chapter);
}
