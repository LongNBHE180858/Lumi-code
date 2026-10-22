package com.lumi.service.mapper;

import com.lumi.domain.CourseOrder;
import com.lumi.domain.InstructorWallet;
import com.lumi.domain.WalletTransaction;
import com.lumi.service.dto.CourseOrderDTO;
import com.lumi.service.dto.InstructorWalletDTO;
import com.lumi.service.dto.WalletTransactionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WalletTransaction} and its DTO {@link WalletTransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface WalletTransactionMapper extends EntityMapper<WalletTransactionDTO, WalletTransaction> {
    @Mapping(target = "wallet", source = "wallet", qualifiedByName = "instructorWalletId")
    @Mapping(target = "order", source = "order", qualifiedByName = "courseOrderId")
    WalletTransactionDTO toDto(WalletTransaction s);

    @Named("instructorWalletId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InstructorWalletDTO toDtoInstructorWalletId(InstructorWallet instructorWallet);

    @Named("courseOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CourseOrderDTO toDtoCourseOrderId(CourseOrder courseOrder);
}
