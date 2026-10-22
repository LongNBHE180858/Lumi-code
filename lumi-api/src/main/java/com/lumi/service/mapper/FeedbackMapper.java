package com.lumi.service.mapper;

import com.lumi.domain.Course;
import com.lumi.domain.Feedback;
import com.lumi.domain.Lesson;
import com.lumi.service.dto.CourseDTO;
import com.lumi.service.dto.FeedbackDetailDTO;
import com.lumi.service.dto.FeedbackListDTO;
import com.lumi.service.dto.LessonDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Feedback} and its DTOs {@link FeedbackListDTO} and {@link FeedbackDetailDTO}.
 */
@Mapper(componentModel = "spring")
public interface FeedbackMapper {

    @Mapping(target = "senderIdentity", ignore = true)
    FeedbackListDTO toListDto(Feedback feedback);

    @Mapping(target = "course", source = "course", qualifiedByName = "courseSummary")
    @Mapping(target = "lesson", source = "lesson", qualifiedByName = "lessonSummary")
    @Mapping(target = "senderIdentity", ignore = true)
    FeedbackDetailDTO toDetailDto(Feedback feedback);

    @AfterMapping
    default void populateSenderIdentity(Feedback feedback, @MappingTarget FeedbackListDTO dto) {
        if (feedback.getUser() != null) {
            dto.setSenderIdentity(feedback.getUser().getLogin());
        } else {
            dto.setSenderIdentity("Tài khoản đã bị xóa");
        }
    }

    @AfterMapping
    default void populateSenderIdentity(Feedback feedback, @MappingTarget FeedbackDetailDTO dto) {
        if (feedback.getUser() != null) {
            dto.setSenderIdentity(feedback.getUser().getLogin());
        } else {
            dto.setSenderIdentity("Tài khoản đã bị xóa");
        }
    }

    @Named("courseSummary")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "isDeleted", source = "isDeleted")
    CourseDTO toDtoCourseSummary(Course course);

    @Named("lessonSummary")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "isPreview", source = "isPreview")
    LessonDTO toDtoLessonSummary(Lesson lesson);
}
