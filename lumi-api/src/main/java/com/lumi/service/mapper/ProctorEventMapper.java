package com.lumi.service.mapper;

import com.lumi.domain.ProctorEvent;
import com.lumi.domain.ProctorSession;
import com.lumi.service.dto.ProctorEventDTO;
import com.lumi.service.dto.ProctorSessionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProctorEvent} and its DTO {@link ProctorEventDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProctorEventMapper extends EntityMapper<ProctorEventDTO, ProctorEvent> {
    @Mapping(target = "session", source = "session", qualifiedByName = "proctorSessionId")
    ProctorEventDTO toDto(ProctorEvent s);

    @Named("proctorSessionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProctorSessionDTO toDtoProctorSessionId(ProctorSession proctorSession);
}
