package com.lumi.service.mapper;

import com.lumi.domain.ContentReport;
import com.lumi.domain.User;
import com.lumi.service.dto.ContentReportDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ContentReport} and its DTO {@link ContentReportDTO}.
 */
@Mapper(componentModel = "spring")
public interface ContentReportMapper extends EntityMapper<ContentReportDTO, ContentReport> {
    @Mapping(target = "reporter", source = "reporter", qualifiedByName = "userLogin")
    @Mapping(target = "assignedTo", source = "assignedTo", qualifiedByName = "userLogin")
    @Mapping(target = "resolvedBy", source = "resolvedBy", qualifiedByName = "userLogin")
    ContentReportDTO toDto(ContentReport s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
