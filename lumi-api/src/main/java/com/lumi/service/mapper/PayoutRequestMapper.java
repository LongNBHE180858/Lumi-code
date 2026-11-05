package com.lumi.service.mapper;

import com.lumi.domain.InstructorWallet;
import com.lumi.domain.PayoutRequest;
import com.lumi.domain.User;
import com.lumi.service.dto.InstructorWalletDTO;
import com.lumi.service.dto.PayoutRequestDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PayoutRequest} and its DTO {@link PayoutRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface PayoutRequestMapper extends EntityMapper<PayoutRequestDTO, PayoutRequest> {
    @Mapping(target = "wallet", source = "wallet", qualifiedByName = "instructorWalletId")
    @Mapping(target = "processedBy", source = "processedBy", qualifiedByName = "userLogin")
    PayoutRequestDTO toDto(PayoutRequest s);

    @Named("instructorWalletId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InstructorWalletDTO toDtoInstructorWalletId(InstructorWallet instructorWallet);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
