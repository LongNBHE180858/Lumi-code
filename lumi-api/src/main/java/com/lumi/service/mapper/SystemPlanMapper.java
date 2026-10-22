package com.lumi.service.mapper;

import com.lumi.domain.SystemPlan;
import com.lumi.service.dto.SystemPlanDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SystemPlan} and its DTO {@link SystemPlanDTO}.
 */
@Mapper(componentModel = "spring")
public interface SystemPlanMapper extends EntityMapper<SystemPlanDTO, SystemPlan> {}
