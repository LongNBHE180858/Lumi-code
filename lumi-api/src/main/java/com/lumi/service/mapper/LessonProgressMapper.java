package com.lumi.service.mapper;

import com.lumi.domain.CourseEnrollment;
import com.lumi.domain.Lesson;
import com.lumi.domain.LessonProgress;
import com.lumi.service.dto.CourseEnrollmentDTO;
import com.lumi.service.dto.LessonDTO;
import com.lumi.service.dto.LessonProgressDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LessonProgress} and its DTO {@link LessonProgressDTO}.
 */
@Mapper(componentModel = "spring")
public interface LessonProgressMapper extends EntityMapper<LessonProgressDTO, LessonProgress> {
    @Mapping(target = "enrollment", source = "enrollment", qualifiedByName = "courseEnrollmentId")
    @Mapping(target = "lesson", source = "lesson", qualifiedByName = "lessonId")
    LessonProgressDTO toDto(LessonProgress s);

    @Named("courseEnrollmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CourseEnrollmentDTO toDtoCourseEnrollmentId(CourseEnrollment courseEnrollment);

    @Named("lessonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LessonDTO toDtoLessonId(Lesson lesson);
}
