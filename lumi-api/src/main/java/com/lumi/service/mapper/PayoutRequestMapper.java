package com.lumi.service.mapper;

import com.lumi.domain.InstructorWallet;
import com.lumi.domain.PayoutRequest;
import com.lumi.service.dto.InstructorWalletDTO;
import com.lumi.service.dto.PayoutRequestDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PayoutRequest} and its DTO {@link PayoutRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface PayoutRequestMapper extends EntityMapper<PayoutRequestDTO, PayoutRequest> {
    @Mapping(target = "wallet", source = "wallet", qualifiedByName = "instructorWalletId")
    PayoutRequestDTO toDto(PayoutRequest s);

    @Named("instructorWalletId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InstructorWalletDTO toDtoInstructorWalletId(InstructorWallet instructorWallet);
}
