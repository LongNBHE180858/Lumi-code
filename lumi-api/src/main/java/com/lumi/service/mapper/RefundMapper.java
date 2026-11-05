package com.lumi.service.mapper;

import com.lumi.domain.CourseOrder;
import com.lumi.domain.Payment;
import com.lumi.domain.Refund;
import com.lumi.domain.User;
import com.lumi.service.dto.CourseOrderDTO;
import com.lumi.service.dto.PaymentDTO;
import com.lumi.service.dto.RefundDTO;
import com.lumi.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Refund} and its DTO {@link RefundDTO}.
 */
@Mapper(componentModel = "spring")
public interface RefundMapper extends EntityMapper<RefundDTO, Refund> {
    @Mapping(target = "order", source = "order", qualifiedByName = "courseOrderId")
    @Mapping(target = "payment", source = "payment", qualifiedByName = "paymentId")
    @Mapping(target = "processedBy", source = "processedBy", qualifiedByName = "userLogin")
    RefundDTO toDto(Refund s);

    @Named("courseOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CourseOrderDTO toDtoCourseOrderId(CourseOrder courseOrder);

    @Named("paymentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PaymentDTO toDtoPaymentId(Payment payment);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
