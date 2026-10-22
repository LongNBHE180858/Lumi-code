package com.lumi.service.mapper;

import com.lumi.domain.QuestionBank;
import com.lumi.domain.User;
import com.lumi.service.dto.QuestionBankDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link QuestionBank} and its DTO {@link QuestionBankDTO}.
 */
@Mapper(componentModel = "spring")
public interface QuestionBankMapper extends EntityMapper<QuestionBankDTO, QuestionBank> {
    @Mapping(target = "instructor", source = "instructor", qualifiedByName = "userLogin")
    QuestionBankDTO toDto(QuestionBank s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
