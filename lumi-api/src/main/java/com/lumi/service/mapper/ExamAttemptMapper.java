package com.lumi.service.mapper;

import com.lumi.domain.Exam;
import com.lumi.domain.ExamAttempt;
import com.lumi.domain.User;
import com.lumi.service.dto.ExamAttemptDTO;
import com.lumi.service.dto.ExamDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ExamAttempt} and its DTO {@link ExamAttemptDTO}.
 */
@Mapper(componentModel = "spring")
public interface ExamAttemptMapper extends EntityMapper<ExamAttemptDTO, ExamAttempt> {
    @Mapping(target = "student", source = "student", qualifiedByName = "userLogin")
    @Mapping(target = "exam", source = "exam", qualifiedByName = "examTitle")
    ExamAttemptDTO toDto(ExamAttempt s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("examTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    ExamDTO toDtoExamTitle(Exam exam);
}
