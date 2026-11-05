package com.lumi.service.mapper;

import com.lumi.domain.InstructorWallet;
import com.lumi.domain.User;
import com.lumi.service.dto.InstructorWalletDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InstructorWallet} and its DTO {@link InstructorWalletDTO}.
 */
@Mapper(componentModel = "spring")
public interface InstructorWalletMapper extends EntityMapper<InstructorWalletDTO, InstructorWallet> {
    @Mapping(target = "instructor", source = "instructor", qualifiedByName = "userLogin")
    InstructorWalletDTO toDto(InstructorWallet s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
