package com.lumi.service.mapper;

import com.lumi.domain.CourseOrder;
import com.lumi.domain.Payment;
import com.lumi.service.dto.CourseOrderDTO;
import com.lumi.service.dto.PaymentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Payment} and its DTO {@link PaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper extends EntityMapper<PaymentDTO, Payment> {
    @Mapping(target = "order", source = "order", qualifiedByName = "courseOrderId")
    PaymentDTO toDto(Payment s);

    @Named("courseOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CourseOrderDTO toDtoCourseOrderId(CourseOrder courseOrder);
}
