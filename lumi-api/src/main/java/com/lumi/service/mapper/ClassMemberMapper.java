package com.lumi.service.mapper;

import com.lumi.domain.ClassMember;
import com.lumi.domain.StudyClass;
import com.lumi.domain.User;
import com.lumi.service.dto.ClassMemberDTO;
import com.lumi.service.dto.StudyClassDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ClassMember} and its DTO {@link ClassMemberDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClassMemberMapper extends EntityMapper<ClassMemberDTO, ClassMember> {
    @Mapping(target = "student", source = "student", qualifiedByName = "userLogin")
    @Mapping(target = "studyClass", source = "studyClass", qualifiedByName = "studyClassName")
    ClassMemberDTO toDto(ClassMember s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("studyClassName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    StudyClassDTO toDtoStudyClassName(StudyClass studyClass);
}
