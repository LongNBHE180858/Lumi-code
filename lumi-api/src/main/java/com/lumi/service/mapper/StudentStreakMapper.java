package com.lumi.service.mapper;

import com.lumi.domain.StudentStreak;
import com.lumi.domain.User;
import com.lumi.service.dto.StudentStreakDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StudentStreak} and its DTO {@link StudentStreakDTO}.
 */
@Mapper(componentModel = "spring")
public interface StudentStreakMapper extends EntityMapper<StudentStreakDTO, StudentStreak> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    StudentStreakDTO toDto(StudentStreak s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
