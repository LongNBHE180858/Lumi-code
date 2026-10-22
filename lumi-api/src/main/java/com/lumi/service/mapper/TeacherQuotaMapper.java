package com.lumi.service.mapper;

import com.lumi.domain.SystemPlan;
import com.lumi.domain.TeacherQuota;
import com.lumi.domain.User;
import com.lumi.service.dto.SystemPlanDTO;
import com.lumi.service.dto.TeacherQuotaDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TeacherQuota} and its DTO {@link TeacherQuotaDTO}.
 */
@Mapper(componentModel = "spring")
public interface TeacherQuotaMapper extends EntityMapper<TeacherQuotaDTO, TeacherQuota> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "currentPlan", source = "currentPlan", qualifiedByName = "systemPlanName")
    TeacherQuotaDTO toDto(TeacherQuota s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("systemPlanName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SystemPlanDTO toDtoSystemPlanName(SystemPlan systemPlan);
}
