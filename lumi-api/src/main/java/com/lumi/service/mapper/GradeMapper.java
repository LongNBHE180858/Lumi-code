package com.lumi.service.mapper;

import com.lumi.domain.Grade;
import com.lumi.service.dto.GradeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Grade} and its DTO {@link GradeDTO}.
 */
@Mapper(componentModel = "spring")
public interface GradeMapper extends EntityMapper<GradeDTO, Grade> {}
