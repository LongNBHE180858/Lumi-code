package com.lumi.service.mapper;

import com.lumi.domain.Assignment;
import com.lumi.domain.AssignmentStudent;
import com.lumi.domain.User;
import com.lumi.service.dto.AssignmentDTO;
import com.lumi.service.dto.AssignmentStudentDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AssignmentStudent} and its DTO {@link AssignmentStudentDTO}.
 */
@Mapper(componentModel = "spring")
public interface AssignmentStudentMapper extends EntityMapper<AssignmentStudentDTO, AssignmentStudent> {
    @Mapping(target = "student", source = "student", qualifiedByName = "userLogin")
    @Mapping(target = "assignment", source = "assignment", qualifiedByName = "assignmentTitle")
    AssignmentStudentDTO toDto(AssignmentStudent s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("assignmentTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    AssignmentDTO toDtoAssignmentTitle(Assignment assignment);
}
