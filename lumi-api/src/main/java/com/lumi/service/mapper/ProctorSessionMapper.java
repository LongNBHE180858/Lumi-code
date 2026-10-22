package com.lumi.service.mapper;

import com.lumi.domain.ExamAttempt;
import com.lumi.domain.ProctorSession;
import com.lumi.service.dto.ExamAttemptDTO;
import com.lumi.service.dto.ProctorSessionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProctorSession} and its DTO {@link ProctorSessionDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProctorSessionMapper extends EntityMapper<ProctorSessionDTO, ProctorSession> {
    @Mapping(target = "attempt", source = "attempt", qualifiedByName = "examAttemptId")
    ProctorSessionDTO toDto(ProctorSession s);

    @Named("examAttemptId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ExamAttemptDTO toDtoExamAttemptId(ExamAttempt examAttempt);
}
